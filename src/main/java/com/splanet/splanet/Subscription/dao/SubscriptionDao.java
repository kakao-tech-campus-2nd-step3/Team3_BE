package com.splanet.splanet.Subscription.dao;

import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.Subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SubscriptionDao {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionDao(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // 구독 조회
    public Optional<Subscription> findActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, Subscription.Status.ACTIVE);
    }

    // 구독 취소
    public void cancelSubscription(Subscription subscription) {
        subscription.cancel();
        subscriptionRepository.save(subscription);
    }

    // 구독하기
    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}