package com.splanet.splanet.plan.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanRequestDto {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private Boolean accessibility = true;
    @Builder.Default
    private Boolean isCompleted = false;
}