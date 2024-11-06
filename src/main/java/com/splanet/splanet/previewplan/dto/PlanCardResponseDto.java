package com.splanet.splanet.previewplan.dto;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.previewplan.entity.PlanCard;
import com.splanet.splanet.core.exception.ErrorCode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record PlanCardResponseDto(
        String deviceId,
        String groupId,
        String cardId,
        String title,
        String description,
        long startTimestamp,
        long endTimestamp
) {
  public static PlanCardResponseDto from(PlanCard planCard) {
    if (planCard.getStartDate() == null || planCard.getEndDate() == null) {
      throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT, "StartDate or EndDate cannot be null");
    }

    long startTimestamp = parseDateToTimestamp(planCard.getStartDate());
    long endTimestamp = parseDateToTimestamp(planCard.getEndDate());

    return new PlanCardResponseDto(
            planCard.getDeviceId(),
            planCard.getGroupId(),
            planCard.getCardId(),
            planCard.getTitle(),
            planCard.getDescription(),
            startTimestamp,
            endTimestamp
    );
  }

  private static long parseDateToTimestamp(String date) {
    if (date == null) {
      throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT, "Date cannot be null");
    }

    try {
      return Long.parseLong(date);
    } catch (NumberFormatException e) {
      try {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime.toEpochSecond(ZoneOffset.of("+9"));
      } catch (DateTimeParseException ex) {
        throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT, "Invalid date format");
      }
    }
  }
}