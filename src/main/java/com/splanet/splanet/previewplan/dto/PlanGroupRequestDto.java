package com.splanet.splanet.previewplan.dto;

import java.util.List;

public record PlanGroupRequestDto(
        String deviceId,
        String groupId,
        List<PlanCardRequestDto> planCards
) {
}