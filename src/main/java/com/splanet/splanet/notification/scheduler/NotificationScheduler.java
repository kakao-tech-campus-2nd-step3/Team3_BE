package com.splanet.splanet.notification.scheduler;

import com.splanet.splanet.notification.entity.FcmToken;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final PlanRepository planRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 10000)  
    public void sendScheduledNotifications() {

        LocalDateTime now = LocalDateTime.now();

        List<Plan> upcomingPlans = planRepository.findUpcomingPlans(now);

        for (Plan plan : upcomingPlans) {
            Long userId = plan.getUser().getId();
            List<FcmToken> fcmTokens = fcmTokenRepository.findByUserId(userId);

            for (FcmToken fcmToken : fcmTokens) {
                if (Boolean.TRUE.equals(fcmToken.getIsNotificationEnabled())) {
                    LocalDateTime notificationTime = plan.getStartDate().minusMinutes(fcmToken.getNotificationOffset());

                    if (notificationTime.isAfter(now.minusMinutes(5)) && notificationTime.isBefore(now.plusMinutes(1))) {
                        boolean alreadySent = notificationLogRepository.findByFcmTokenIdAndPlanId(fcmToken.getId(), plan.getId()).isPresent();

                        if (!alreadySent) {
                            notificationService.sendNotification(fcmToken, plan);
                        }
                    }
                }
            }
        }
    }
}
