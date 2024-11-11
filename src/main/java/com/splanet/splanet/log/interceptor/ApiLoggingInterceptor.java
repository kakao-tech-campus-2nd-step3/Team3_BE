package com.splanet.splanet.log.interceptor;

import com.splanet.splanet.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

  private final LogService logService;

  public ApiLoggingInterceptor(LogService logService) {
    this.logService = logService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestPath = request.getRequestURI();
    String headers = getHeadersAsString(request);
    String deviceId = request.getHeader("deviceId"); // 예시로 Header에서 deviceId 추출

    // 로그 기록 (userId는 로그인 여부에 따라 추가)
    Long userId = (Long) request.getAttribute("userId"); // 로그인한 사용자라면 userId가 있을 것으로 가정
    logService.recordApiRequestLog(userId, deviceId, requestPath, headers);

    return true;
  }

  private String getHeadersAsString(HttpServletRequest request) {
    StringBuilder headers = new StringBuilder();
    request.getHeaderNames().asIterator().forEachRemaining(headerName ->
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", "));
    return headers.toString();
  }
}