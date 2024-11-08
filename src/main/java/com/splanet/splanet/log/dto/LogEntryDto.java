package com.splanet.splanet.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class LogEntryDto {
  private String eventType;
  private Long userId;
  private String deviceId;
  private Instant timestamp;

  public LogEntryDto(String eventType, Long userId, String deviceId) {
    this.eventType = eventType;
    this.userId = userId;
    this.deviceId = deviceId;
    this.timestamp = Instant.now();
  }
}