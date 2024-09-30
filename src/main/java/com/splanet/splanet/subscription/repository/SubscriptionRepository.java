package com.splanet.splanet.subscription.repository;

import com.splanet.splanet.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserIdAndStatusOrderByStartDateDesc(Long userId, Subscription.Status status);
}