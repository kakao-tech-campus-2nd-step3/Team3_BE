package com.splanet.splanet.log.aspect;

import com.splanet.splanet.log.service.LogService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginSuccessAspect {

  private final LogService logService;

  @Autowired
  public LoginSuccessAspect(LogService logService) {
    this.logService = logService;
  }

  @AfterReturning(pointcut = "execution(* com.splanet.splanet.oauth.CustomOAuth2UserService.loadUser(..))", returning = "oAuth2User")
  public void logLoginSuccess(OAuth2User oAuth2User) {
    Long userId = (Long) oAuth2User.getAttributes().get("id");
    String deviceId = oAuth2User.getName();
    logService.recordLoginLog(userId, deviceId);
  }
}