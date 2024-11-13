package com.splanet.splanet.log.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = "LOG_PATH=./splanet-test.log")
class LogServiceTest {

  @Value("${LOG_PATH}")
  private String logPath;

  private LogService logService;

  @BeforeEach
  void setUp() throws IOException {
    assertThat(logPath).isNotNull();

    File logFile = new File(logPath);
    Files.createDirectories(Paths.get(logFile.getParent()));

    if (!logFile.exists()) {
      logFile.createNewFile();
    }

    logService = new LogService(logPath);
  }

  @Test
  void 로그인_성공_로그_기록_확인() {
    Long userId = 1L;
    String deviceId = "testDeviceId";
    String requestPath = "/test/login";
    String headers = "User-Agent: TestAgent, Accept: */*";

    logService.recordLoginLog(userId, deviceId, requestPath, headers);

    File logFile = new File(logPath);
    assertTrue(logFile.exists(), "로그 파일이 생성되지 않았습니다.");

    try {
      String content = Files.readString(logFile.toPath());
      assertThat(content).contains("eventType: LOGIN_SUCCESS", "userId: 1", "deviceId: testDeviceId", "requestPath: /test/login", "headers: User-Agent: TestAgent, Accept: */*");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void api_요청_로그_기록_확인() {
    // given
    Long userId = 2L;
    String deviceId = "apiTestDeviceId";
    String requestPath = "/test/api";
    String headers = "User-Agent: ApiTestAgent, Accept: application/json";
    int statusCode = 200;

    // when
    logService.recordApiRequestLog(userId, deviceId, requestPath, headers, statusCode);

    // then
    File logFile = new File(logPath);
    assertTrue(logFile.exists(), "로그 파일이 생성되지 않았습니다.");

    // 로그 파일 내용 확인
    try {
      String content = Files.readString(logFile.toPath());
      assertThat(content).contains(
              "eventType: API_REQUEST",
              "userId: 2",
              "deviceId: apiTestDeviceId",
              "requestPath: /test/api",
              "headers: User-Agent: ApiTestAgent, Accept: application/json",
              "statusCode: 200"
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}