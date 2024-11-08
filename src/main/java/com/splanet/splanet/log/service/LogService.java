package com.splanet.splanet.log.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@Service
public class LogService {

  private final String logPath;

  public LogService(@Value("${LOG_PATH}") String logPath) {
    this.logPath = logPath;
  }

  // 로그인 성공 시 로그 기록
  public void recordLoginLog(Long userId, String deviceId) {
    String timestamp = getCurrentKstTimestamp();
    String logMessage = String.format("eventType: LOGIN_SUCCESS, userId: %s, deviceId: %s, timestamp: %s",
            userId, deviceId, timestamp);
    writeLog(logMessage);
  }

  // 실제 로그 파일에 기록
  private void writeLog(String logMessage) {
    try (FileWriter writer = new FileWriter(logPath, true)) {
      writer.write(logMessage + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 현재 KST 시간을 ISO 형식으로 반환
  private String getCurrentKstTimestamp() {
    return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}