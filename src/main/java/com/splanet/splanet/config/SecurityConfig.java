package com.splanet.splanet.config;

import com.splanet.splanet.core.properties.OAuth2Properties;
import com.splanet.splanet.jwt.service.TokenService;
import com.splanet.splanet.oauth.CustomOAuth2UserService;
import com.splanet.splanet.jwt.JwtAuthenticationFilter;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.oauth.OAuth2AuthenticationSuccessHandler;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenService tokenService;
  private final OAuth2Properties oAuth2Properties;

  public SecurityConfig(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, TokenService tokenService, OAuth2Properties oAuth2Properties) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.tokenService = tokenService;
    this.oAuth2Properties = oAuth2Properties;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService())
                    )
                    .successHandler(oAuth2AuthenticationSuccessHandler())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private CustomOAuth2UserService customOAuth2UserService() {
    return new CustomOAuth2UserService();
  }

  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
    return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider, userRepository, tokenService, oAuth2Properties);
  }
}