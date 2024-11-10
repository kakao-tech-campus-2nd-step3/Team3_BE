package com.splanet.splanet.oauth;

import com.splanet.splanet.log.service.LogService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final LogService logService;

  public CustomOAuth2UserService(LogService logService) {
    this.logService = logService;
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

    // 로그인 시 로그 기록
    logService.recordLoginLog(userId, deviceId);

    return oAuth2User;
  }
}