package com.splanet.splanet.oauth;

import com.splanet.splanet.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final LogService logService;
  private HttpServletRequest request; // HttpServletRequest 필드 추가

  @Autowired
  public CustomOAuth2UserService(LogService logService) {
    this.logService = logService;
  }

  @Autowired // HttpServletRequest를 별도로 @Autowired로 주입
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attributes = oAuth2User.getAttributes();

    if (!attributes.containsKey("id")) {
      throw new OAuth2AuthenticationException("카카오 사용자 정보가 없습니다.");
    }

    Long userId = (Long) attributes.get("id");
    String deviceId = oAuth2User.getName();

    // 요청 경로 및 헤더 로깅 (예시)
    String requestPath = request.getRequestURI();
    String headers = getHeadersAsString(request);

    logService.recordLoginLog(userId, deviceId, requestPath, headers);

    return oAuth2User;
  }

  private String getHeadersAsString(HttpServletRequest request) {
    StringBuilder headers = new StringBuilder();
    request.getHeaderNames().asIterator().forEachRemaining(headerName ->
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", "));
    return headers.toString();
  }
}