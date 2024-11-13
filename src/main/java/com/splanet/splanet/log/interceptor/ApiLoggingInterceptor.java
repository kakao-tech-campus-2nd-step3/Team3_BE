package com.splanet.splanet.log.interceptor;

import com.splanet.splanet.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

  private final LogService logService;
  private final List<String> loggableHeaders = List.of("host", "referer", "user-agent", "accept");

  public ApiLoggingInterceptor(LogService logService) {
    this.logService = logService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestPath = request.getRequestURI();
    String headers = getLoggableHeadersAsString(request);

    // 세션에서 userId와 deviceId 가져오기
    Long userId = (Long) request.getSession().getAttribute("userId");
    String deviceId = (String) request.getSession().getAttribute("deviceId");

    // 상태 코드 가져오기
    int statusCode = response.getStatus();

    // 로그 기록
    logService.recordApiRequestLog(userId, deviceId, requestPath, headers, statusCode);

    return true;
  }

  private String getLoggableHeadersAsString(HttpServletRequest request) {
    StringBuilder headers = new StringBuilder();
    loggableHeaders.forEach(headerName -> {
      String headerValue = request.getHeader(headerName);
      if (headerValue != null) {
        headers.append(headerName).append(": ").append(headerValue).append(", ");
      }
    });
    return headers.toString();
  }
}