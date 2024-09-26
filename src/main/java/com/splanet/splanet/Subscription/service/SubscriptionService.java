package com.splanet.splanet.Subscription.service;

import com.splanet.splanet.Subscription.dao.SubscriptionDao;
import com.splanet.splanet.Subscription.dto.SubscriptionDto;
import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionDao subscriptionDao;

    public SubscriptionService(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    // 구독 조회
    public ResponseEntity<SubscriptionDto> getSubscription(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Subscription subscription = subscriptionDao.findActiveSubscription(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPSTION_NOT_FOUND));

        SubscriptionDto responseDto = SubscriptionDto.fromSubscription(subscription);
        return ResponseEntity.ok()
                .header("Message", "구독 정보가 성공적으로 조회되었습니다.")
                .body(responseDto);
    }

    // 구독 취소
    public ResponseEntity<String> cancelSubscription(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Subscription subscription = subscriptionDao.findActiveSubscription(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPSTION_NOT_FOUND));

        if (subscription.getStatus() == Subscription.Status.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED);
        }

        subscriptionDao.cancelSubscription(subscription);
        return ResponseEntity.ok("구독이 성공적으로 취소되었습니다.");
    }

    // 구독하기
    public ResponseEntity<SubscriptionDto> subscribe(Long userId, Subscription.Type type) {
        // 구독 객체 생성
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(java.time.LocalDateTime.now())
                .endDate(java.time.LocalDateTime.now().plusMonths(type == Subscription.Type.MONTHLY ? 1 : 12))
                .build();

        Subscription savedSubscription = subscriptionDao.saveSubscription(subscription);

        SubscriptionDto responseDto = SubscriptionDto.fromSubscription(savedSubscription);

        return ResponseEntity.ok()
                .header("Message", "구독이 성공적으로 구매되었습니다.")
                .body(responseDto);
    }
}