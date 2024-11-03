package com.splanet.splanet.plan.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

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
}