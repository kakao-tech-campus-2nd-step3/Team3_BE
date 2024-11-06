package com.splanet.splanet.previewplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.entity.PlanCard;
import com.splanet.splanet.previewplan.repository.PlanCardRepository;
import com.splanet.splanet.previewplan.repository.PlanGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreviewPlanServiceTest {

    @Mock
    private PlanCardRepository planCardRepository;

    @Mock
    private PlanGroupRepository planGroupRepository;

    @InjectMocks
    private PreviewPlanService previewPlanService;

    @Test
    @DisplayName("성공적으로 PlanCard 저장 - 타임스탬프 형식")
    void savePlanCardWithTimestampFormat() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        PlanCardRequestDto requestDto = new PlanCardRequestDto("제목", "설명", "1730728800", "1730728800");
        when(planCardRepository.save(any(PlanCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PlanCardResponseDto response = previewPlanService.savePlanCard(deviceId, groupId, requestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.startTimestamp()).isEqualTo(1730728800L);
        verify(planCardRepository, times(1)).save(any(PlanCard.class));
    }

    @Test
    @DisplayName("성공적으로 PlanCard 저장 - LocalDateTime 형식")
    void savePlanCardWithLocalDateTimeFormat() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        PlanCardRequestDto requestDto = new PlanCardRequestDto("제목", "설명", "2024-11-05T09:00:00", "2024-11-05T12:00:00");
        when(planCardRepository.save(any(PlanCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PlanCardResponseDto response = previewPlanService.savePlanCard(deviceId, groupId, requestDto);

        // then
        assertThat(response).isNotNull();
        verify(planCardRepository, times(1)).save(any(PlanCard.class));
    }

    @Test
    @DisplayName("저장 실패 - 잘못된 LocalDateTime 형식")
    void savePlanCardWithInvalidLocalDateTimeFormat() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        PlanCardRequestDto requestDto = new PlanCardRequestDto("제목", "설명", "2024/11/05 09:00:00", "2024-11-05T12:00:00");

        // when & then
        assertThatThrownBy(() -> previewPlanService.savePlanCard(deviceId, groupId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    @DisplayName("PlanCard 조회 성공")
    void getPlanCardSuccess() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        String cardId = "card1";
        String redisKey = deviceId + ":" + groupId + ":" + cardId;

        String startDate = "2024-11-05T09:00:00";
        String endDate = "2024-11-05T10:00:00";

        PlanCard planCard = PlanCard.builder()
                .customKey(redisKey)
                .startDate(startDate)  // startDate 설정
                .endDate(endDate)      // endDate 설정
                .build();

        when(planCardRepository.findById(redisKey)).thenReturn(Optional.of(planCard));

        // when
        PlanCardResponseDto response = previewPlanService.getPlanCard(deviceId, groupId, cardId);

        // then
        assertThat(response).isNotNull();
        verify(planCardRepository, times(1)).findById(redisKey);
    }

    @Test
    @DisplayName("PlanCard 조회 실패 - 존재하지 않음")
    void getPlanCardNotFound() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        String cardId = "card1";
        String redisKey = deviceId + ":" + groupId + ":" + cardId;
        when(planCardRepository.findById(redisKey)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> previewPlanService.getPlanCard(deviceId, groupId, cardId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("PlanCard 삭제 성공")
    void deletePlanCardSuccess() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        String cardId = "card1";
        String redisKey = deviceId + ":" + groupId + ":" + cardId;
        PlanCard planCard = PlanCard.builder().customKey(redisKey).build();
        when(planCardRepository.findById(redisKey)).thenReturn(Optional.of(planCard));

        // when
        previewPlanService.deletePlanCard(deviceId, groupId, cardId);

        // then
        verify(planCardRepository, times(1)).delete(planCard);
    }

    @Test
    @DisplayName("PlanCard 삭제 실패 - 존재하지 않음")
    void deletePlanCardNotFound() {
        // given
        String deviceId = "device1";
        String groupId = "group1";
        String cardId = "card1";
        String redisKey = deviceId + ":" + groupId + ":" + cardId;
        when(planCardRepository.findById(redisKey)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> previewPlanService.deletePlanCard(deviceId, groupId, cardId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }
}
