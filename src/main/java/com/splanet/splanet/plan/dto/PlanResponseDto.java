package com.splanet.splanet.plan.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.splanet.splanet.plan.entity.Plan;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneOffset;

@Getter
@Builder
public class PlanResponseDto {
  private Long id;
  private String title;
  private String description;
  private long startTimestamp; // 타임스탬프 사용
  private long endTimestamp;   // 타임스탬프 사용

  @JsonIgnore
  private Boolean accessibility;
  @JsonIgnore
  private Boolean isCompleted;
  @JsonIgnore
  private long createdAt;
  @JsonIgnore
  private long updatedAt;

  public PlanResponseDto toResponseDto(Plan plan) {
    return PlanResponseDto.builder()
            .id(plan.getId())
            .title(plan.getTitle())
            .description(plan.getDescription())
            .startTimestamp(plan.getStartDate().toEpochSecond(ZoneOffset.of("+9")))
            .endTimestamp(plan.getEndDate().toEpochSecond(ZoneOffset.of("+9")))
            .createdAt(plan.getCreatedAt().toEpochSecond(ZoneOffset.of("+9")))
            .updatedAt(plan.getUpdatedAt().toEpochSecond(ZoneOffset.of("+9")))
            .build();
  }
}