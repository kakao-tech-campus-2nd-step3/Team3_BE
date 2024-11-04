package com.splanet.splanet.plan.mapper;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class PlanMapper {
  private static final ZoneOffset SEOUL_ZONE_OFFSET = ZoneOffset.of("+09:00");

  private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), SEOUL_ZONE_OFFSET);
  }

  private long convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return 0L;
    }
    return localDateTime.toEpochSecond(SEOUL_ZONE_OFFSET);
  }

  public Plan toEntity(PlanRequestDto requestDto, User user) {
    return Plan.builder()
            .user(user)
            .title(requestDto.getTitle())
            .description(requestDto.getDescription())
            .startDate(convertTimestampToLocalDateTime(requestDto.getStartTimestamp()))
            .endDate(convertTimestampToLocalDateTime(requestDto.getEndTimestamp()))
            .accessibility(requestDto.getAccessibility())
            .isCompleted(requestDto.getIsCompleted())
            .build();
  }

  public PlanResponseDto toResponseDto(Plan plan) {
    if (plan == null) {
      throw new IllegalArgumentException("Plan cannot be null");
    }

    return PlanResponseDto.builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .description(plan.getDescription())
            .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
            .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
            .createdAt(convertLocalDateTimeToTimestamp(plan.getCreatedAt()))
            .updatedAt(convertLocalDateTimeToTimestamp(plan.getUpdatedAt()))
            .build();
  }
}