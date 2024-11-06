package com.splanet.splanet.gpt.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanJsonDto {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;

  public PlanJsonDto(Long id, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }
}