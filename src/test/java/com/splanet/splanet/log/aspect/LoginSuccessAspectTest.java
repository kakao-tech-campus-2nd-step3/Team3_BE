package com.splanet.splanet.log.aspect;

import com.splanet.splanet.log.service.LogService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.mockito.Mockito.*;

@Aspect
class LoginSuccessAspectTest {

  @Mock
  private LogService logService;

  @Mock
  private OAuth2User oAuth2User;

  @InjectMocks
  private LoginSuccessAspect loginSuccessAspect;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void 로그인_성공_시_로그_기록() {
    // given
    Long userId = 1L;
    String deviceId = "test-device-id";
    when(oAuth2User.getAttributes()).thenReturn(Map.of("id", userId));
    when(oAuth2User.getName()).thenReturn(deviceId);

    // when
    loginSuccessAspect.logLoginSuccess(oAuth2User);

    // then
    verify(logService).recordLoginLog(userId, deviceId);
  }
}