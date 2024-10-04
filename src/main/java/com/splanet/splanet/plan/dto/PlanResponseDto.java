package com.splanet.splanet.plan.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean accessibility;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
