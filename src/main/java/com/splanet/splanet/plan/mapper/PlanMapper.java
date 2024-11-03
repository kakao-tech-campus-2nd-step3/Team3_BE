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

  private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.of("+9"));
  }

  private long convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
    return localDateTime.toEpochSecond(ZoneOffset.of("+9"));
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
    return PlanResponseDto.builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .description(plan.getDescription())
            .startTimestamp(convertLocalDateTimeToTimestamp(plan.getStartDate()))
            .endTimestamp(convertLocalDateTimeToTimestamp(plan.getEndDate()))
            .accessibility(plan.getAccessibility())
            .isCompleted(plan.getIsCompleted())
            .createdAt(convertLocalDateTimeToTimestamp(plan.getCreatedAt()))
            .updatedAt(convertLocalDateTimeToTimestamp(plan.getUpdatedAt()))
            .build();
  }
}