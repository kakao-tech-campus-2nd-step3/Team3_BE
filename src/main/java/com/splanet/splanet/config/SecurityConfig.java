package com.splanet.splanet.config;

import com.splanet.splanet.core.properties.OAuth2Properties;
import com.splanet.splanet.jwt.service.TokenService;
import com.splanet.splanet.log.service.LogService;
import com.splanet.splanet.oauth.CustomOAuth2UserService;
import com.splanet.splanet.jwt.JwtAuthenticationFilter;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.oauth.OAuth2AuthenticationSuccessHandler;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenService tokenService;
  private final OAuth2Properties oAuth2Properties;
  private final LogService logService; // LogService 추가

  public SecurityConfig(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, TokenService tokenService, OAuth2Properties oAuth2Properties, LogService logService) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.tokenService = tokenService;
    this.oAuth2Properties = oAuth2Properties;
    this.logService = logService; // LogService 초기화
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://www.splanet.co.kr", "https://api.splanet.co.kr"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
    configuration.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private CustomOAuth2UserService customOAuth2UserService() {
    return new CustomOAuth2UserService(logService); // LogService를 전달
  }

  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
    return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider, userRepository, tokenService, oAuth2Properties, logService);
  }

  private JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtTokenProvider);
  }
}