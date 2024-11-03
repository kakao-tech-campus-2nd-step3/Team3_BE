package com.splanet.splanet.plan.service;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.mapper.PlanMapper;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

  private final PlanRepository planRepository;
  private final PlanMapper planMapper;
  private final UserRepository userRepository;
  private final PreviewPlanService previewPlanService;

  private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.of("+9"));
  }

  private long convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      throw new IllegalArgumentException("LocalDateTime 값이 null입니다.");
    }
    return localDateTime.toEpochSecond(ZoneOffset.of("+9"));
  }

  @Transactional
  public PlanResponseDto createPlan(Long userId, PlanRequestDto requestDto) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 타임스탬프를 LocalDateTime으로 변환
    LocalDateTime startDate = convertTimestampToLocalDateTime(requestDto.getStartTimestamp());
    LocalDateTime endDate = convertTimestampToLocalDateTime(requestDto.getEndTimestamp());

    Plan plan = Plan.builder()
            .title(requestDto.getTitle())
            .description(requestDto.getDescription())
            .startDate(startDate)
            .endDate(endDate)
            .user(user)
            .accessibility(requestDto.getAccessibility())
            .isCompleted(requestDto.getIsCompleted())
            .build();

    planRepository.save(plan);

    // LocalDateTime을 타임스탬프로 변환하여 반환
    return PlanResponseDto.builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .description(plan.getDescription())
            .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
            .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
            .build();
  }

  @Transactional
  public List<PlanResponseDto> saveGroupCardsToUser(Long userId, String deviceId, String groupId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    Set<PlanCardResponseDto> previewCards = previewPlanService.getPlanCardsByGroup(deviceId, groupId);

    List<Plan> savedPlans = previewCards.stream()
            .map(previewCard -> convertToPlan(previewCard, user))
            .map(planRepository::save)
            .collect(Collectors.toList());

    return savedPlans.stream()
            .map(plan -> PlanResponseDto.builder()
                    .id(plan.getId())
                    .title(plan.getTitle())
                    .description(plan.getDescription())
                    .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
                    .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
                    .build())
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PlanResponseDto getPlanById(Long planId) {
    Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    if (plan.getStartDate() == null || plan.getEndDate() == null) {
      throw new IllegalArgumentException("시작 또는 종료 날짜가 null입니다.");
    }

    return planMapper.toResponseDto(plan);
  }
  @Transactional(readOnly = true)
  public List<PlanResponseDto> getAllPlansByUserId(Long userId) {
    List<Plan> plans = planRepository.findAllByUserId(userId);

    return plans.stream()
            .map(plan -> PlanResponseDto.builder()
                    .id(plan.getId())
                    .title(plan.getTitle())
                    .description(plan.getDescription())
                    .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
                    .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
                    .build())
            .collect(Collectors.toList());
  }

  @Transactional
  public PlanResponseDto updatePlan(Long planId, PlanRequestDto requestDto) {
    Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    LocalDateTime startDate = convertTimestampToLocalDateTime(requestDto.getStartTimestamp());
    LocalDateTime endDate = convertTimestampToLocalDateTime(requestDto.getEndTimestamp());

    plan = plan.toBuilder()
            .title(requestDto.getTitle())
            .description(requestDto.getDescription())
            .startDate(startDate)
            .endDate(endDate)
            .accessibility(requestDto.getAccessibility())
            .isCompleted(requestDto.getIsCompleted())
            .build();

    planRepository.save(plan);

    return PlanResponseDto.builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .description(plan.getDescription())
            .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
            .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
            .build();
  }

  @Transactional
  public void deletePlan(Long planId) {
    Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    planRepository.delete(plan);
  }

  private Plan convertToPlan(PlanCardResponseDto previewCard, User user) {
    LocalDateTime startDate = convertTimestampToLocalDateTime(previewCard.startTimestamp());
    LocalDateTime endDate = convertTimestampToLocalDateTime(previewCard.endTimestamp());

    return Plan.builder()
            .title(previewCard.title())
            .description(previewCard.description())
            .startDate(startDate)
            .endDate(endDate)
            .user(user)
            .build();
  }

  @Transactional(readOnly = true)
  public List<PlanResponseDto> getAllFuturePlansByUserId(Long userId) {
    LocalDateTime now = LocalDateTime.now();
    List<Plan> futurePlans = planRepository.findAllByUserIdAndStartDateAfter(userId, now);

    return futurePlans.stream()
            .map(plan -> PlanResponseDto.builder()
                    .id(plan.getId())
                    .title(plan.getTitle())
                    .description(plan.getDescription())
                    .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
                    .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
                    .build())
            .collect(Collectors.toList());
  }
}