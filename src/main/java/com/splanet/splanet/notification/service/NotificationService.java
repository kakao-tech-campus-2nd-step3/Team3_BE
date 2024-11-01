package com.splanet.splanet.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.notification.entity.NotificationLog;
import com.splanet.splanet.notification.repository.FcmTokenRepository;
import com.splanet.splanet.notification.repository.NotificationLogRepository;
import com.splanet.splanet.plan.entity.Plan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(FcmTokenRepository fcmTokenRepository, FirebaseMessaging firebaseMessaging, NotificationLogRepository notificationLogRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.firebaseMessaging = firebaseMessaging;
        this.notificationLogRepository = notificationLogRepository;
    }

    public void sendNotification(FcmToken fcmToken, Plan plan) {
        String title = "곧 시작하는 플랜: " + plan.getTitle();
        String body = "곧 시작하는 플랜이 있어요! " + plan.getDescription();

        Notification notification = new Notification(title, body);

        Message message = Message.builder()
                .setToken(fcmToken.getToken())
                .setNotification(notification)
                .putData("title", plan.getTitle())
                .putData("title", plan.getDescription())
                .putData("startDate", plan.getStartDate().toString())
                .build();
        try {
            String response = firebaseMessaging.send(message);
            log.info("알림을 정상적으로 전송하였습니다. : {}", response);

            // 알림 전송 기록 저장
            NotificationLog logEntry = NotificationLog.builder()
                    .fcmToken(fcmToken)
                    .plan(plan)
                    .sentAt(LocalDateTime.now())
                    .build();
            notificationLogRepository.save(logEntry);

        } catch (Exception e) {
            log.error("FCM 알림 전송 실패 ", e);
        }
    }

    // 알림 테스트를 위한 메소드 (추후 삭제)
    public void sendTestNotification(Long userId) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findByUserId(userId);

        for (FcmToken fcmToken : fcmTokens) {
            Notification notification = new Notification("테스트 알림", "이것은 테스트 알림입니다.");

            Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .setNotification(notification)
                    .build();

            try {
                String response = firebaseMessaging.send(message);
                log.info("Successfully sent message: {}", response);
            } catch (Exception e) {
                log.error("Failed to send FCM notification", e);
            }
        }
    }
}
