package com.splanet.splanet.notification.scheduler;

import com.splanet.splanet.core.util.QueryPerformanceService;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.notification.entity.NotificationLog;
import com.splanet.splanet.notification.repository.FcmTokenRepository;
import com.splanet.splanet.notification.repository.NotificationLogRepository;
import com.splanet.splanet.notification.service.NotificationService;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final PlanRepository planRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationService notificationService;
    private final QueryPerformanceService queryPerformanceService;

    private static final ZoneId ZONE_ID_SEOUL = ZoneId.of("Asia/Seoul");

    @Scheduled(fixedRate = 60000)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now(ZONE_ID_SEOUL);
        log.info("Scheduler started at {}", now);

        List<Plan> upcomingPlans = planRepository.findUpcomingPlans(now);
        log.info("Found {} upcoming plans", upcomingPlans.size());

        Set<Long> userIds = upcomingPlans.stream()
                .map(plan -> plan.getUser().getId())
                .collect(Collectors.toSet());
        log.info("Collected user IDs: {}", userIds);

        List<FcmToken> allFcmTokens = fcmTokenRepository.findByUserIdIn(userIds);
        log.info("Fetched {} FCM tokens", allFcmTokens.size());

        Map<Long, List<FcmToken>> userFcmTokenMap = allFcmTokens.stream()
                .collect(Collectors.groupingBy(fcmToken -> fcmToken.getUser().getId()));

        Set<Long> planIds = upcomingPlans.stream()
                .map(Plan::getId)
                .collect(Collectors.toSet());
        log.info("Collected plan IDs: {}", planIds);

        Set<Long> fcmTokenIds = allFcmTokens.stream()
                .map(FcmToken::getId)
                .collect(Collectors.toSet());
        log.info("Collected FCM token IDs: {}", fcmTokenIds);

        List<NotificationLog> notificationLogs = notificationLogRepository.findByFcmTokenIdInAndPlanIdIn(fcmTokenIds, planIds);
        log.info("Found {} notification logs for existing notifications", notificationLogs.size());

        Set<String> sentNotificationKeys = notificationLogs.stream()
                .map(log -> log.getFcmToken().getId() + ":" + log.getPlan().getId())
                .collect(Collectors.toSet());

        for (Plan plan : upcomingPlans) {
            Long userId = plan.getUser().getId();
            List<FcmToken> fcmTokens = userFcmTokenMap.getOrDefault(userId, Collections.emptyList());
            log.info("Processing plan ID {} for user ID {}, FCM tokens: {}", plan.getId(), userId, fcmTokens.size());

            for (FcmToken fcmToken : fcmTokens) {
                if (Boolean.TRUE.equals(fcmToken.getIsNotificationEnabled())) {
                    LocalDateTime notificationTime = plan.getStartDate().atZone(ZONE_ID_SEOUL).toLocalDateTime().minusMinutes(fcmToken.getNotificationOffset());
                    log.info("Evaluating notification for FCM token ID {} at notificationTime: {}", fcmToken.getId(), notificationTime);

                    if (notificationTime.isAfter(now.minusMinutes(5)) && notificationTime.isBefore(now.plusMinutes(1))) {
                        String notificationKey = fcmToken.getId() + ":" + plan.getId();

                        if (!sentNotificationKeys.contains(notificationKey)) {
                            log.info("Sending notification for FCM token ID {} and plan ID {}", fcmToken.getId(), plan.getId());
                            notificationService.sendNotification(fcmToken, plan);
                        } else {
                            log.info("Notification already sent for FCM token ID {} and plan ID {}", fcmToken.getId(), plan.getId());
                        }
                    } else {
                        log.info("Notification time {} does not match the required range for now: {}", notificationTime, now);
                    }
                } else {
                    log.info("Notification disabled for FCM token ID {}", fcmToken.getId());
                }
            }
        }
    }
}
