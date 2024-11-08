package com.splanet.splanet.plan.mapper;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public Plan toEntity(PlanRequestDto requestDto, User user) {
        return Plan.builder()
                .user(user)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .accessibility(requestDto.getAccessibility())
                .isCompleted(requestDto.getIsCompleted())
                .build();
    }

    public PlanResponseDto toResponseDto(Plan plan) {
        return PlanResponseDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .accessibility(plan.getAccessibility())
                .isCompleted(plan.getIsCompleted())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}