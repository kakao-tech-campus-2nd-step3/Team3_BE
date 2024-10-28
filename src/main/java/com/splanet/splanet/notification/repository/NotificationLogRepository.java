package com.splanet.splanet.notification.repository;

import com.splanet.splanet.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByFcmTokenIdIn(Collection<Long> fcmTokenIds);
    List<NotificationLog> findByFcmTokenIdInAndPlanIdIn(Collection<Long> fcmTokenIds, Collection<Long> planIds);

}
