package com.splanet.splanet.previewplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.dto.PlanGroupWithCardsResponseDto;
import com.splanet.splanet.previewplan.entity.PlanCard;
import com.splanet.splanet.previewplan.entity.PlanGroup;
import com.splanet.splanet.previewplan.repository.PlanCardRepository;
import com.splanet.splanet.previewplan.repository.PlanGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreviewPlanService {

    private static final String PLAN_GROUP_PREFIX = "planGroup:";
    private static final String PLAN_CARD_PREFIX = "planCard:";
    private static final int PLAN_CARD_PREFIX_LENGTH = PLAN_CARD_PREFIX.length();

    private final RedisTemplate<String, Object> redisTemplate;
    private final PlanCardRepository planCardRepository;
    private final PlanGroupRepository planGroupRepository;

    public PlanCardResponseDto savePlanCard(String deviceId, String groupId, PlanCardRequestDto planCardRequestDto) {
        String cardId = PlanCard.generateId();
        String customKey = generateCustomKey(deviceId, groupId, cardId);

        PlanCard newPlanCard = buildPlanCard(customKey, deviceId, groupId, cardId, planCardRequestDto);
        planCardRepository.save(newPlanCard);
        updatePlanGroup(deviceId, groupId, cardId, true);
        return PlanCardResponseDto.from(newPlanCard);
    }

    public PlanCardResponseDto getPlanCard(String deviceId, String groupId, String cardId) {
        String redisKey = generateCustomKey(deviceId, groupId, cardId);
        return planCardRepository.findById(redisKey)
                .map(PlanCardResponseDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    }

    public PlanCardResponseDto updatePlanCard(String deviceId, String groupId, String cardId, PlanCardRequestDto planCardRequestDto) {
        String redisKey = generateCustomKey(deviceId, groupId, cardId);
        PlanCard existingPlanCard = planCardRepository.findById(redisKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

        PlanCard updatedCard = buildPlanCard(redisKey, deviceId, groupId, cardId, planCardRequestDto);
        planCardRepository.save(updatedCard);
        return PlanCardResponseDto.from(updatedCard);
    }

    public void deletePlanCard(String deviceId, String groupId, String cardId) {
        String redisKey = generateCustomKey(deviceId, groupId, cardId);
        PlanCard existingPlanCard = planCardRepository.findById(redisKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

        planCardRepository.delete(existingPlanCard);
        updatePlanGroup(deviceId, groupId, cardId, false);
    }

    public Set<PlanGroupWithCardsResponseDto> getPreviewPlans(String deviceId) {
        Set<String> groupKeys = scanKeysByPattern(PLAN_GROUP_PREFIX + deviceId + ":*");

        return groupKeys.stream()
                .map(groupKey -> {
                    String groupId = extractGroupIdFromKey(groupKey);

                    Set<PlanCardResponseDto> planCards = scanKeysByPattern(PLAN_CARD_PREFIX + deviceId + ":" + groupId + ":*").stream()
                            .map(this::getPlanCardByKey)
                            .collect(Collectors.toSet());

                    return new PlanGroupWithCardsResponseDto(deviceId, groupId, planCards);
                })
                .collect(Collectors.toSet());
    }

    private PlanCard buildPlanCard(String customKey, String deviceId, String groupId, String cardId, PlanCardRequestDto planCardRequestDto) {
        return PlanCard.builder()
                .customKey(customKey)
                .deviceId(deviceId)
                .groupId(groupId)
                .cardId(cardId)
                .title(planCardRequestDto.title())
                .description(planCardRequestDto.description())
                .startDate(planCardRequestDto.startDate())
                .endDate(planCardRequestDto.endDate())
                .build();
    }

    private void updatePlanGroup(String deviceId, String groupId, String cardId, boolean add) {
        String groupKey = generateGroupKey(deviceId, groupId);
        PlanGroup planGroup = planGroupRepository.findById(groupKey)
                .orElse(PlanGroup.builder()
                        .deviceId(deviceId)
                        .groupId(groupId)
                        .planCardIds(new HashSet<>())
                        .build());

        if (add) {
            planGroup.getPlanCardIds().add(cardId);
        } else {
            planGroup.getPlanCardIds().remove(cardId);
        }
        planGroupRepository.save(planGroup);
    }

    private Set<String> scanKeysByPattern(String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(100).build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.REDIS_SCAN_FAILED, "패턴으로 SCAN을 할 수 없습니다: " + pattern);
        }

        return keys;
    }

    public Set<PlanCardResponseDto> getPlanCardsByGroup(String deviceId, String groupId) {
        Set<String> cardKeys = scanKeysByPattern(PLAN_CARD_PREFIX + deviceId + ":" + groupId + ":*");

        return cardKeys.stream()
                .map(this::getPlanCardByKey)
                .collect(Collectors.toSet());
    }

    private PlanCardResponseDto getPlanCardByKey(String redisKey) {
        String key = redisKey.substring(PLAN_CARD_PREFIX_LENGTH);
        return planCardRepository.findById(key)
                .map(PlanCardResponseDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    }

    private String generateCustomKey(String deviceId, String groupId, String cardId) {
        return deviceId + ":" + groupId + ":" + cardId;
    }

    private String generateGroupKey(String deviceId, String groupId) {
        return deviceId + ":" + groupId;
    }

    private String extractGroupIdFromKey(String groupKey) {
        return groupKey.split(":")[2];
    }
}
