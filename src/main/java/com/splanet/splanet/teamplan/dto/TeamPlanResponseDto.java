package com.splanet.splanet.teamplan.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamPlanResponseDto {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean accessibility;
  private Boolean isCompleted;  // 완료 여부 추가
  private LocalDateTime createdAt;  // 생성일시 추가
  private LocalDateTime updatedAt;  // 수정일시 추가
}