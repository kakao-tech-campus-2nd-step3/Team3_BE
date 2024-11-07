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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    @Transactional
    public PlanResponseDto createPlan(Long userId, PlanRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Plan plan = planMapper.toEntity(requestDto, user);
        planRepository.save(plan);
        return planMapper.toResponseDto(plan);
    }

    @Transactional
    public List<PlanResponseDto> saveGroupCardsToUser(Long userId, String deviceId, String groupId) { // Redis에 있던 데이터를 실제 유저의 Plan 테이블로 저장
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Set<PlanCardResponseDto> previewCards = previewPlanService.getPlanCardsByGroup(deviceId, groupId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        List<Plan> savedPlans = previewCards.stream()
                .map(previewCard -> convertToPlan(previewCard, user, formatter))
                .map(planRepository::save)
                .collect(Collectors.toList());

        return savedPlans.stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
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

    private Plan convertToPlan(PlanCardResponseDto previewCard, User user, DateTimeFormatter formatter) {
        LocalDateTime startDate = LocalDateTime.parse(previewCard.startDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(previewCard.endDate(), formatter);

        PlanRequestDto planRequestDto = PlanRequestDto.builder()
                .title(previewCard.title())
                .description(previewCard.description())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return planMapper.toEntity(planRequestDto, user);
    }

    @Transactional(readOnly = true)
    public List<PlanResponseDto> getAllFuturePlansByUserId(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Plan> futurePlans = planRepository.findAllByUserIdAndStartDateAfter(userId, now);
        return futurePlans.stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}