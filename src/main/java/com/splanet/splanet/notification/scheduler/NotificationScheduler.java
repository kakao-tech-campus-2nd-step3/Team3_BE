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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final PlanRepository planRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationService notificationService;
    private final QueryPerformanceService queryPerformanceService;


    @Scheduled(fixedRate = 300000)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();

        List<Plan> upcomingPlans = planRepository.findUpcomingPlans(now);

        Set<Long> userIds = upcomingPlans.stream()
                .map(plan -> plan.getUser().getId())
                .collect(Collectors.toSet());

        List<FcmToken> allFcmTokens = fcmTokenRepository.findByUserIdIn(userIds);

        Map<Long, List<FcmToken>> userFcmTokenMap = allFcmTokens.stream()
                .collect(Collectors.groupingBy(fcmToken -> fcmToken.getUser().getId()));

        Set<Long> planIds = upcomingPlans.stream()
                .map(Plan::getId)
                .collect(Collectors.toSet());

        Set<Long> fcmTokenIds = allFcmTokens.stream()
                .map(FcmToken::getId)
                .collect(Collectors.toSet());

        List<NotificationLog> notificationLogs = notificationLogRepository.findByFcmTokenIdInAndPlanIdIn(fcmTokenIds, planIds);

        Set<String> sentNotificationKeys = notificationLogs.stream()
                .map(log -> log.getFcmToken().getId() + ":" + log.getPlan().getId())
                .collect(Collectors.toSet());

        for (Plan plan : upcomingPlans) {
            Long userId = plan.getUser().getId();
            List<FcmToken> fcmTokens = userFcmTokenMap.getOrDefault(userId, Collections.emptyList());

            for (FcmToken fcmToken : fcmTokens) {
                if (Boolean.TRUE.equals(fcmToken.getIsNotificationEnabled())) {
                    LocalDateTime notificationTime = plan.getStartDate().minusMinutes(fcmToken.getNotificationOffset());

                    if (notificationTime.isAfter(now.minusMinutes(5)) && notificationTime.isBefore(now.plusMinutes(1))) {
                        String notificationKey = fcmToken.getId() + ":" + plan.getId();

                        if (!sentNotificationKeys.contains(notificationKey)) {
                            notificationService.sendNotification(fcmToken, plan);
                        }
                    }
                }
            }
        }
    }
}
