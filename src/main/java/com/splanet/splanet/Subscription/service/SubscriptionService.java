package com.splanet.splanet.Subscription.service;

import com.splanet.splanet.Subscription.dao.SubscriptionDao;
import com.splanet.splanet.Subscription.entity.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SubscriptionService {

    private final SubscriptionDao subscriptionDao;

    public SubscriptionService(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    // 구독 조회
    public ResponseEntity<Map<String, Object>> getSubscription(Long userId) {
        Subscription subscription = subscriptionDao.findActiveSubscription(userId)
                .orElseThrow(() -> new IllegalArgumentException("구독 정보를 찾을 수 없습니다."));

        Map<String, Object> response = Map.of(
                "subscription_type", subscription.getType().name().toLowerCase(),
                "start_date", subscription.getStartDate(),
                "end_date", subscription.getEndDate(),
                "status", subscription.getStatus().name().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }

    // 구독 취소
    public void cancelSubscription(Long userId) {
        Subscription subscription = subscriptionDao.findActiveSubscription(userId)
                .orElseThrow(() -> new IllegalArgumentException("구독 정보를 찾을 수 없습니다."));
        subscriptionDao.cancelSubscription(subscription);
    }

    // 구독하기
    public Map<String, Object> subscribe(Long userId, Subscription.Type type) {
        // 구독 객체 생성
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(java.time.LocalDateTime.now())
                .endDate(java.time.LocalDateTime.now().plusMonths(type == Subscription.Type.MONTHLY ? 1 : 12))
                .build();

        // 구독 저장
        Subscription savedSubscription = subscriptionDao.saveSubscription(subscription);

        // 응답 맵 생성
        return Map.of(
                "message", "구독이 성공적으로 구매되었습니다.",
                "subscription", Map.of(
                        "id", savedSubscription.getId(),
                        "start_date", savedSubscription.getStartDate(),
                        "end_date", savedSubscription.getEndDate()
                )
        );
    }
}