package com.splanet.splanet.plan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanRequestDto {
  private String title;
  private String description;
  private long startTimestamp; // 타임스탬프 사용
  private long endTimestamp;   // 타임스탬프 사용

  @Builder.Default
  private Boolean accessibility = true;
  @Builder.Default
  private Boolean isCompleted = false;
}