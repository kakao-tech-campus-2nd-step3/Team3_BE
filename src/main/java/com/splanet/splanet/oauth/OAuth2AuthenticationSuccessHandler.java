package com.splanet.splanet.oauth;

import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
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

    String token = jwtTokenProvider.createToken(user.getId(), user.getNickname());

    String redirectUrl = "http://localhost:5173?token=" + token;
    response.sendRedirect(redirectUrl);
  }

  private String generateUniqueNickname(String nickname) {
    return userRepository.findByNickname(nickname)
            .map(existingUser -> nickname + "#" + System.currentTimeMillis() % 10000) // 중복되면 임의값 추가
            .orElse(nickname);
  }
}