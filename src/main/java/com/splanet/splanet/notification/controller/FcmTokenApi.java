package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.dto.FcmTokenRequest;
import com.splanet.splanet.notification.dto.FcmTokenUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/fcm")
@Tag(name = "FCM", description = "FCM 토큰 관리 API")
public interface FcmTokenApi {

    @PostMapping("/register")
    ResponseEntity<String> registerFcmToken(
            @AuthenticationPrincipal Long userId,
            @RequestBody FcmTokenRequest fcmTokenRequest
    );

    @PutMapping("/update/notification-enabled")
    ResponseEntity<String> updateNotificationEnabled(
            @RequestParam String token,
            @RequestParam Boolean isNotificationEnabled
    );

    @PutMapping("/update/notification-offset")
    ResponseEntity<String> updateNotificationOffset(
            @RequestParam String token,
            @RequestParam Integer notificationOffset
    );

    @DeleteMapping("/delete")
    ResponseEntity<String> deleteFcmToken(
            @RequestParam String token
    );
}
