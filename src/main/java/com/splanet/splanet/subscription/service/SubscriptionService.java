package com.splanet.splanet.subscription.service;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // 구독 조회
    public ResponseEntity<SubscriptionResponse> getSubscription(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 가장 최근 ACTIVE 구독
        Subscription subscription = subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPSTION_NOT_FOUND));

        SubscriptionResponse response = new SubscriptionResponse("구독 정보가 성공적으로 조회되었습니다.", subscription);
        return ResponseEntity.ok(response);
    }

    // 구독 취소
    public ResponseEntity<String> cancelSubscription(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Subscription subscription = subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPSTION_NOT_FOUND));

        if (subscription.getStatus() == Subscription.Status.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED);
        }

        subscription.cancel();
        subscriptionRepository.save(subscription);
        return ResponseEntity.ok("{\"message\": \"구독이 성공적으로 취소되었습니다.\"}");
    }

    // 구독하기
    public ResponseEntity<SubscriptionResponse> subscribe(Long userId, SubscriptionRequest request) {
        // 구독 객체 생성
        Subscription subscription = createSubscription(userId, request.getType());
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return createSubscriptionResponse(savedSubscription);
    }

    private Subscription createSubscription(Long userId, Subscription.Type type) {
        return Subscription.builder()
                .userId(userId)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(java.time.LocalDateTime.now())
                .endDate(java.time.LocalDateTime.now().plusMonths(type == Subscription.Type.MONTHLY ? 1 : 12))
                .build();
    }

    private ResponseEntity<SubscriptionResponse> createSubscriptionResponse(Subscription savedSubscription) {
        SubscriptionResponse response = new SubscriptionResponse("구독이 성공적으로 구매되었습니다.", savedSubscription);
        return ResponseEntity.ok(response);
    }
}