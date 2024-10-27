package com.splanet.splanet.notification.repository;

import com.splanet.splanet.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    Optional<NotificationLog> findByFcmTokenIdAndPlanId(Long fcmTokenId, Long planId);
}
