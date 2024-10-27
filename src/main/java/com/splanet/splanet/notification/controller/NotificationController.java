package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;



    @PostMapping("/send/{userId}")
    public ResponseEntity<Map<String, String>> sendTestNotification(@PathVariable Long userId) {
        notificationService.sendTestNotification(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification sent to user with ID: " + userId);
        return ResponseEntity.ok(response);
    }
}
