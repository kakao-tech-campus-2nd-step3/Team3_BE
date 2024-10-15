package com.splanet.splanet.previewplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.entity.PlanCard;
import com.splanet.splanet.previewplan.entity.PlanGroup;
import com.splanet.splanet.previewplan.repository.PlanCardRepository;
import com.splanet.splanet.previewplan.repository.PlanGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PreviewPlanServiceTest {

    @Mock
    private PlanCardRepository planCardRepository;

    @Mock
    private PlanGroupRepository planGroupRepository;

    @InjectMocks
    private PreviewPlanService previewPlanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 플랜카드_성공적으로_저장됨() {
        // given
        PlanCardRequestDto requestDto = new PlanCardRequestDto("title", "description", "2024-10-10T10:00:00", "2024-10-10T12:00:00");
        PlanCard newPlanCard = PlanCard.builder()
                .customKey("deviceId:groupId:cardId")
                .deviceId("deviceId")
                .groupId("groupId")
                .cardId("cardId")
                .title(requestDto.title())
                .description(requestDto.description())
                .startDate(requestDto.startDate())
                .endDate(requestDto.endDate())
                .build();

        when(planCardRepository.save(any(PlanCard.class))).thenReturn(newPlanCard);

        // when
        PlanCardResponseDto result = previewPlanService.savePlanCard("deviceId", "groupId", requestDto);

        // then
        assertEquals("title", result.title());
        assertEquals("description", result.description());
        assertEquals("2024-10-10T10:00:00", result.startDate());
        verify(planCardRepository, times(1)).save(any(PlanCard.class));
    }

    @Test
    void 플랜카드_찾을_수_없음_예외발생() {
        // given
        when(planCardRepository.findById(anyString())).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            previewPlanService.getPlanCard("deviceId", "groupId", "cardId");
        });

        assertEquals(ErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 플랜카드_성공적으로_수정됨() {
        // given
        PlanCard existingPlanCard = PlanCard.builder()
                .customKey("deviceId:groupId:cardId")
                .deviceId("deviceId")
                .groupId("groupId")
                .cardId("cardId")
                .title("oldTitle")
                .description("oldDescription")
                .startDate("2024-10-10T10:00:00")
                .endDate("2024-10-10T12:00:00")
                .build();

        when(planCardRepository.findById(anyString())).thenReturn(Optional.of(existingPlanCard));

        PlanCardRequestDto updateDto = new PlanCardRequestDto("newTitle", "newDescription", "2024-11-10T10:00:00", "2024-11-10T12:00:00");

        // when
        PlanCardResponseDto updatedCard = previewPlanService.updatePlanCard("deviceId", "groupId", "cardId", updateDto);

        // then
        assertEquals("newTitle", updatedCard.title());
        assertEquals("newDescription", updatedCard.description());
        verify(planCardRepository, times(1)).save(any(PlanCard.class));
    }

    @Test
    void 플랜카드_삭제_성공() {
        // given
        PlanCard existingPlanCard = PlanCard.builder()
                .customKey("deviceId:groupId:cardId")
                .deviceId("deviceId")
                .groupId("groupId")
                .cardId("cardId")
                .build();

        when(planCardRepository.findById(anyString())).thenReturn(Optional.of(existingPlanCard));

        // when
        previewPlanService.deletePlanCard("deviceId", "groupId", "cardId");

        // then
        verify(planCardRepository, times(1)).delete(existingPlanCard);
    }

    @Test
    void 플랜그룹_성공적으로_저장됨() {
        // given
        Set<String> planCardIds = new HashSet<>();
        PlanGroup planGroup = PlanGroup.builder()
                .deviceId("deviceId")
                .groupId("groupId")
                .planCardIds(planCardIds)
                .build();

        when(planGroupRepository.findById(anyString())).thenReturn(Optional.of(planGroup));

        // when
        previewPlanService.savePlanCard("deviceId", "groupId", new PlanCardRequestDto("title", "description", "2024-10-10T10:00:00", "2024-10-10T12:00:00"));

        // then
        verify(planGroupRepository, times(1)).save(any(PlanGroup.class));
    }

    @Test
    void 예외처리_잘못된_카드키() {
        // given
        when(planCardRepository.findById(anyString())).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            previewPlanService.getPlanCard("deviceId", "groupId", "wrongCardId");
        });

        assertEquals(ErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
    }
}
