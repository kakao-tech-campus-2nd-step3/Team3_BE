package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.dto.FcmTokenRequest;
import com.splanet.splanet.notification.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmTokenController implements FcmTokenApi {

    private final FcmTokenService fcmTokenService;

    @Override
    public ResponseEntity<String> registerFcmToken(Long userId, FcmTokenRequest fcmTokenRequest) {
        fcmTokenService.registerFcmToken(userId, fcmTokenRequest.token());
        return ResponseEntity.ok("FCM token 생성 완료");
    }

    @Override
    public ResponseEntity<String> updateNotificationEnabled(String token, Boolean isNotificationEnabled) {
        fcmTokenService.updateNotificationEnabled(token, isNotificationEnabled);
        return ResponseEntity.ok("FCM 알림 여부 수정 완료");
    }

    @Override
    public ResponseEntity<String> updateNotificationOffset(String token, Integer notificationOffset) {
        fcmTokenService.updateNotificationOffset(token, notificationOffset);
        return ResponseEntity.ok("FCM 알림 오프셋 수정 완료");
    }

    @Override
    public ResponseEntity<String> deleteFcmToken(String token) {
        fcmTokenService.deleteFcmToken(token);
        return ResponseEntity.ok("FCM token 삭제 완료");
    }
}
