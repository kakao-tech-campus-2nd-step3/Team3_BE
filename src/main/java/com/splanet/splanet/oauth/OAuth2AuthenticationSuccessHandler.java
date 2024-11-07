package com.splanet.splanet.oauth;

import com.splanet.splanet.core.properties.OAuth2Properties;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.jwt.service.TokenService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;

  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final OAuth2Properties oAuth2Properties;

  public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, TokenService tokenService, OAuth2Properties oAuth2Properties) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
    this.tokenService = tokenService;
    this.oAuth2Properties = oAuth2Properties;
  }



  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    Map<String, Object> attributes = oAuth2User.getAttributes();
    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

    Long kakaoId = (Long) attributes.get("id");
    String nickname = (String) properties.get("nickname");
    String profileImage = (String) properties.get("profile_image_url");

    String uniqueNickname = generateUniqueNickname(nickname);

    User user = userRepository.findByKakaoId(kakaoId)
            .orElseGet(() -> {
              User newUser = User.builder()
                      .kakaoId(kakaoId)
                      .nickname(uniqueNickname)
                      .profileImage(profileImage)
                      .build();
              return userRepository.save(newUser);
            });

    String deviceId = UUID.randomUUID().toString();

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    tokenService.storeRefreshToken(refreshToken, user.getId(), deviceId);


    String redirectUrlWithParams = UriComponentsBuilder.fromUriString(oAuth2Properties.getRedirectUrl())
            .queryParam("access", accessToken)
            .queryParam("refresh", refreshToken)
            .queryParam("deviceId", deviceId)
            .build().toUriString();

    response.sendRedirect(redirectUrlWithParams);
  }

  private String generateUniqueNickname(String nickname) {
    String uniqueSuffix = UUID.randomUUID().toString().split("-")[0];

    return userRepository.findByNickname(nickname)
            .map(existingUser -> nickname + "#" + uniqueSuffix)
            .orElse(nickname);
  }
}