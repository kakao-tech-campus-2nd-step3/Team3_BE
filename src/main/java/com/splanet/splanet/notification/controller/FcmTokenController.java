// src/main/java/com/splanet/splanet/notification/controller/FcmTokenController.java
package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.dto.FcmTokenRequest;
import com.splanet.splanet.notification.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    // FCM 토큰 저장 API
    @PostMapping("/register-token")
    public ResponseEntity<String> registerFcmToken(
            @AuthenticationPrincipal Long userId,
            @RequestBody FcmTokenRequest fcmTokenRequest) {
        fcmTokenService.registerFcmToken(userId, fcmTokenRequest.token());
        return ResponseEntity.ok("FCM token registered successfully.");
    }
}
