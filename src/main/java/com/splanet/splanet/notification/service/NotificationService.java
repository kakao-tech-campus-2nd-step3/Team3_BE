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
import java.time.format.DateTimeFormatter;

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
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH시 mm분");

        String nickname = plan.getUser().getNickname();

        String startTime = plan.getStartDate().toLocalTime().format(timeFormatter);
        String endTime = plan.getEndDate().toLocalTime().format(timeFormatter);

        String title = "🗓️ " + nickname + "님! " + plan.getTitle() + " 시간이에요! ";
        String body = startTime + " - " + endTime + " \n" +
                (plan.getDescription() != null ? plan.getDescription() : " ");

        Notification notification = new Notification(title, body);

        String clickActionUrl = "https://www.splanet.co.kr";

        Message message = Message.builder().setToken(fcmToken.getToken())
                .setNotification(notification)
                .putData("click_action", clickActionUrl)
                .putData("title", plan.getTitle())
                .putData("description", plan.getDescription())
                .putData("startDate", plan.getStartDate().toString())
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("알림을 정상적으로 전송하였습니다. : {}", response);

            NotificationLog logEntry = NotificationLog.builder().fcmToken(fcmToken).plan(plan).sentAt(LocalDateTime.now()).build();
            notificationLogRepository.save(logEntry);

        } catch (Exception e) {
            log.error("FCM 알림 전송 실패 ", e);
        }
    }
}
