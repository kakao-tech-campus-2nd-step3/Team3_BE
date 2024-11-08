package com.splanet.splanet.notification.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.notification.dto.FcmTokenUpdateRequest;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.notification.repository.FcmTokenRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FcmTokenServiceTest {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void FCM토큰_등록_성공() {
        Long userId = 1L;
        String token = "testToken";

        User user = User.builder().id(userId).build();
        FcmToken fcmToken = FcmToken.builder().user(user).token(token).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fcmTokenRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.empty());
        when(fcmTokenRepository.save(any(FcmToken.class))).thenReturn(fcmToken);

        assertDoesNotThrow(() -> fcmTokenService.registerFcmToken(userId, token));
        verify(fcmTokenRepository, times(1)).save(any(FcmToken.class));
    }

    @Test
    void FCM토큰_등록_유저없음_예외발생() {
        Long userId = 1L;
        String token = "testToken";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                fcmTokenService.registerFcmToken(userId, token));
        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    @Test
    void FCM토큰_설정_수정_성공() {
        Long userId = 1L;
        String token = "testToken";
        FcmTokenUpdateRequest request = new FcmTokenUpdateRequest(token, true, 30);

        FcmToken fcmToken = FcmToken.builder()
                .user(User.builder().id(userId).build())
                .token(token)
                .isNotificationEnabled(false)
                .notificationOffset(15)
                .build();

        when(fcmTokenRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.of(fcmToken));

        assertDoesNotThrow(() -> fcmTokenService.updateFcmTokenSettings(userId, request));
        verify(fcmTokenRepository, times(1)).save(any(FcmToken.class));
    }

    @Test
    void FCM토큰_설정_수정_토큰없음_예외발생() {
        Long userId = 1L;
        String token = "testToken";
        FcmTokenUpdateRequest request = new FcmTokenUpdateRequest(token, true, 30);

        when(fcmTokenRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                fcmTokenService.updateFcmTokenSettings(userId, request));
        verify(fcmTokenRepository, never()).save(any(FcmToken.class));
    }

    @Test
    void FCM토큰_삭제_성공() {
        Long userId = 1L;
        String token = "testToken";

        FcmToken fcmToken = FcmToken.builder()
                .user(User.builder().id(userId).build())
                .token(token)
                .build();

        when(fcmTokenRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.of(fcmToken));

        assertDoesNotThrow(() -> fcmTokenService.deleteFcmToken(userId, token));
        verify(fcmTokenRepository, times(1)).delete(fcmToken);
    }

    @Test
    void FCM토큰_삭제_토큰없음_예외발생() {
        Long userId = 1L;
        String token = "testToken";

        when(fcmTokenRepository.findByUserIdAndToken(userId, token)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                fcmTokenService.deleteFcmToken(userId, token));
        verify(fcmTokenRepository, never()).delete(any(FcmToken.class));
    }
}