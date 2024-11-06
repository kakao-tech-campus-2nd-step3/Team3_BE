package com.splanet.splanet.previewplan.dto;

import java.util.Set;

public record PlanGroupWithCardsResponseDto(
        String deviceId,
        String groupId,
        Set<PlanCardResponseDto> planCards
) {
}