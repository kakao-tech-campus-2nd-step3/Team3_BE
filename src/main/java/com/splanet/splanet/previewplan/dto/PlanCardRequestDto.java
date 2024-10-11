package com.splanet.splanet.previewplan.dto;

public record PlanCardRequestDto(
        String title,
        String description,
        String startDate,
        String endDate
) {
}