package com.splanet.splanet.teamplan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TeamPlanRequestDto {
  private String title;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean accessibility;
  private Boolean isCompleted;
}