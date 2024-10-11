package com.splanet.splanet.teamplan.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamPlanResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean accessibility,
        Boolean isCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}