package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send/{userId}")
    @Operation(summary = "푸시 알림 테스트", description = "해당 유저에게 테스트 알림을 전송합니다. (사전에 FCM 토큰 발급 필요)")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        notificationService.sendTestNotification(userId);
        return ResponseEntity.ok("테스트 알림 전송 완료: " + userId);
    }
}
