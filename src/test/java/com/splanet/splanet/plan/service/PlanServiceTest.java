package com.splanet.splanet.plan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.mapper.PlanMapper;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    @InjectMocks
    private PlanService planService;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanMapper planMapper;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 플랜_생성_성공() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .build();
        Plan plan = Plan.builder().title("테스트 플랜").user(user).build();
        PlanResponseDto responseDto = PlanResponseDto.builder().title("테스트 플랜").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(planMapper.toEntity(requestDto, user)).willReturn(plan);
        given(planMapper.toResponseDto(plan)).willReturn(responseDto);

        // when
        PlanResponseDto result = planService.createPlan(userId, requestDto);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 플랜");
        verify(planRepository).save(plan);
    }

    @Test
    void 플랜_생성_유저없음() {
        // given
        Long userId = 1L;
        PlanRequestDto requestDto = PlanRequestDto.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.createPlan(userId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 플랜_조회_성공() {
        // given
        Long planId = 1L;
        Plan plan = Plan.builder().title("테스트 플랜").build();
        PlanResponseDto responseDto = PlanResponseDto.builder().title("테스트 플랜").build();

        given(planRepository.findById(planId)).willReturn(Optional.of(plan));
        given(planMapper.toResponseDto(plan)).willReturn(responseDto);

        // when
        PlanResponseDto result = planService.getPlanById(planId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 플랜");
    }

    @Test
    void 플랜_조회_없음() {
        // given
        Long planId = 1L;
        given(planRepository.findById(planId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    void 플랜_업데이트_성공() {
        // given
        Long planId = 1L;
        Plan existingPlan = Plan.builder()
                .title("기존 플랜")
                .description("기존 설명")
                .build();
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .build();

        Plan updatedPlan = existingPlan.toBuilder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .build();

        PlanResponseDto responseDto = PlanResponseDto.builder()
                .id(planId)
                .title("수정된 플랜")
                .description("수정된 설명")
                .build();

        // Mocking PlanRepository and PlanMapper
        given(planRepository.findById(planId)).willReturn(Optional.of(existingPlan));
        given(planRepository.save(any(Plan.class))).willReturn(updatedPlan);
        given(planMapper.toResponseDto(any(Plan.class))).willReturn(responseDto);

        // when
        PlanResponseDto result = planService.updatePlan(planId, requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("수정된 플랜");
        assertThat(result.getDescription()).isEqualTo("수정된 설명");
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void 플랜_업데이트_플랜없음() {
        // given
        Long planId = 1L;
        PlanRequestDto requestDto = PlanRequestDto.builder().build();

        given(planRepository.findById(planId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.updatePlan(planId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    void 플랜_삭제_성공() {
        // given
        Long planId = 1L;
        Plan plan = Plan.builder().title("테스트 플랜").build();
        given(planRepository.findById(planId)).willReturn(Optional.of(plan));

        // when
        planService.deletePlan(planId);

        // then
        verify(planRepository).delete(plan);
    }

    @Test
    void 플랜_삭제_플랜없음() {
        // given
        Long planId = 1L;
        given(planRepository.findById(planId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }
}
