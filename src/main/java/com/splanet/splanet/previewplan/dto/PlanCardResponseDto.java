package com.splanet.splanet.previewplan.dto;

import com.splanet.splanet.previewplan.entity.PlanCard;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public record PlanCardResponseDto(
        String deviceId,
        String groupId,
        String cardId,
        String title,
        String description,
        long startTimestamp, // 타임스탬프 사용
        long endTimestamp    // 타임스탬프 사용
) {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static PlanCardResponseDto from(PlanCard planCard) {
    // String을 LocalDateTime으로 변환 후 타임스탬프로 변환
    LocalDateTime startDate = LocalDateTime.parse(planCard.getStartDate(), formatter);
    LocalDateTime endDate = LocalDateTime.parse(planCard.getEndDate(), formatter);

    return new PlanCardResponseDto(
            planCard.getDeviceId(),
            planCard.getGroupId(),
            planCard.getCardId(),
            planCard.getTitle(),
            planCard.getDescription(),
            startDate.toEpochSecond(ZoneOffset.of("+9")),
            endDate.toEpochSecond(ZoneOffset.of("+9"))
    );
  }
}