package com.splanet.splanet.previewplan.dto;

import com.splanet.splanet.previewplan.entity.PlanCard;

public record PlanCardResponseDto(
        String deviceId,
        String groupId,
        String cardId,
        String title,
        String description,
        String startDate,
        String endDate
) {
  public static PlanCardResponseDto from(PlanCard planCard) {
    return new PlanCardResponseDto(
            planCard.getDeviceId(),
            planCard.getGroupId(),
            planCard.getCardId(),
            planCard.getTitle(),
            planCard.getDescription(),
            planCard.getStartDate(),
            planCard.getEndDate()
    );
  }
}