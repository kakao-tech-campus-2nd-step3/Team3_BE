package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        notificationService.sendTestNotification(userId);
        return ResponseEntity.ok("Notification sent to user with ID: " + userId);
    }
}
