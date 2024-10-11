package com.splanet.splanet.teamplan.dto;


import java.time.LocalDateTime;

public record TeamPlanRequestDto(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean accessibility,
        Boolean isCompleted
) {}