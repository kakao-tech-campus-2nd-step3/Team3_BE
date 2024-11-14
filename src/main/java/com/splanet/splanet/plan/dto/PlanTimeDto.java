package com.splanet.splanet.plan.dto;

import java.time.LocalDateTime;

public class PlanTimeDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public PlanTimeDto(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
