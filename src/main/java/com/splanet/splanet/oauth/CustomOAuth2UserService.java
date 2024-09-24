package com.splanet.splanet.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    // 여기에서 사용자 정보를 추가 처리할 수 있습니다.
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 필요한 사용자 정보가 있는지 확인
    if (attributes.containsKey("id")) {
      System.out.println("카카오 사용자 ID: " + attributes.get("id"));
    } else {
      throw new OAuth2AuthenticationException("카카오 사용자 정보가 없습니다.");
    }

    return oAuth2User;
  }
}