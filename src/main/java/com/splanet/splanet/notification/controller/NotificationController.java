package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi{

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        notificationService.sendTestNotification(userId);
        return ResponseEntity.ok("테스트 알림 전송 완료: " + userId);
    }
}