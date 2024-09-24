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
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

    String nickname = (String) properties.get("nickname");
    String profileImage = (String) properties.get("profile_image_url");
    String email = (String) kakaoAccount.get("email");

    Optional<User> existingUser = userRepository.findByNickname(nickname);
    if (existingUser.isEmpty()) {
      User newUser = User.builder()
              .nickname(nickname)
              .profileImage(profileImage)
              .build();
      userRepository.save(newUser);
    }
    String token = jwtTokenProvider.createToken(authentication);
    String redirectUrl = "http://localhost:5173?token=" + token;
    System.out.println("Redirecting to: " + redirectUrl); // 디버그를 위해 출력
    response.sendRedirect(redirectUrl);
  }
}