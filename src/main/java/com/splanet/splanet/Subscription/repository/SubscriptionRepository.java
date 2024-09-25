package com.splanet.splanet.Subscription.repository;

import com.splanet.splanet.Subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, Subscription.Status status);
}