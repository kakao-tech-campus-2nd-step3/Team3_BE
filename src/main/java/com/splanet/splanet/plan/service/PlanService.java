package com.splanet.splanet.plan.service;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.mapper.PlanMapper;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    private final UserRepository userRepository;

    @Transactional
    public PlanResponseDto createPlan(Long userId, PlanRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Plan plan = planMapper.toEntity(requestDto, user);
        planRepository.save(plan);
        return planMapper.toResponseDto(plan);
    }

    @Transactional(readOnly = true)
    public PlanResponseDto getPlanById(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
        return planMapper.toResponseDto(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanResponseDto> getAllPlansByUserId(Long userId) {
        List<Plan> plans = planRepository.findAllByUserId(userId);
        return plans.stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanResponseDto updatePlan(Long planId, PlanRequestDto requestDto) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

        plan = plan.toBuilder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .accessibility(requestDto.getAccessibility())
                .isCompleted(requestDto.getIsCompleted())
                .build();

        planRepository.save(plan);
        return planMapper.toResponseDto(plan);
    }

    @Transactional
    public void deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
        planRepository.delete(plan);
    }
}
