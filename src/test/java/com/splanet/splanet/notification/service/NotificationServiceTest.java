package com.splanet.splanet.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.notification.entity.NotificationLog;
import com.splanet.splanet.notification.repository.FcmTokenRepository;
import com.splanet.splanet.notification.repository.NotificationLogRepository;
import com.splanet.splanet.plan.entity.Plan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 알림_전송_성공() {
        FcmToken fcmToken = FcmToken.builder()
                .token("FCM토큰")
                .build();

        Plan plan = mock(Plan.class);
        when(plan.getTitle()).thenReturn("테스트용 플랜");
        when(plan.getDescription()).thenReturn("테스트용 설명");
        when(plan.getStartDate()).thenReturn(LocalDateTime.now());

        try {
            when(firebaseMessaging.send(any(Message.class))).thenReturn("응답");
            doNothing().when(notificationLogRepository).save(any(NotificationLog.class));

            assertDoesNotThrow(() -> notificationService.sendNotification(fcmToken, plan));
            verify(firebaseMessaging, times(1)).send(any(Message.class));
            verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void 알림_전송_실패() {
        FcmToken fcmToken = FcmToken.builder()
                .token("FCM토큰")
                .build();

        Plan plan = mock(Plan.class);
        when(plan.getTitle()).thenReturn("테스트용 플랜");
        when(plan.getDescription()).thenReturn("테스트용 설명");
        when(plan.getStartDate()).thenReturn(LocalDateTime.now());

        try {
            when(firebaseMessaging.send(any(Message.class))).thenThrow(new RuntimeException("FCM 전송 실패"));
            doNothing().when(notificationLogRepository).save(any(NotificationLog.class));

            assertDoesNotThrow(() -> notificationService.sendNotification(fcmToken, plan));
            verify(firebaseMessaging, times(1)).send(any(Message.class));
            verify(notificationLogRepository, times(0)).save(any(NotificationLog.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}