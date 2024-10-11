package com.splanet.splanet.subscription.service;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository , UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // 구독 조회
    public ResponseEntity<SubscriptionResponse> getSubscription(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 가장 최근 ACTIVE 구독
        Subscription subscription = subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        SubscriptionResponse response = new SubscriptionResponse("구독 정보가 성공적으로 조회되었습니다.", subscription);
        return ResponseEntity.ok(response);
    }

    // 구독 취소
    @Transactional
    public ResponseEntity<String> cancelSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Subscription subscription = subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (subscription.getStatus() == Subscription.Status.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED);
        }

        subscription.cancel();

        subscriptionRepository.save(subscription);

        // isPremium 값을 false로 업데이트
        User updatedUser = user.toBuilder()
                .isPremium(false)
                .build();

        userRepository.save(updatedUser);

        return ResponseEntity.ok("{\"message\": \"구독이 성공적으로 취소되었습니다.\"}");
    }

    // 구독하기
    @Transactional
    public ResponseEntity<SubscriptionResponse> subscribe(Long userId, Subscription.Type type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // isPremium 값을 true로 업데이트
        if (!user.getIsPremium()) {
            User updatedUser = user.toBuilder()
                    .isPremium(true)
                    .build();

            userRepository.save(updatedUser);
        }

        Subscription subscription = createSubscription(user, type);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return createSubscriptionResponse(savedSubscription);
    }

    private Subscription createSubscription(User user, Subscription.Type type) {
        return Subscription.builder()
                .user(user)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(type == Subscription.Type.MONTHLY ? 1 : 12))
                .build();
    }

    private ResponseEntity<SubscriptionResponse> createSubscriptionResponse(Subscription savedSubscription) {
        SubscriptionResponse response = new SubscriptionResponse("구독이 성공적으로 구매되었습니다.", savedSubscription);
        return ResponseEntity.ok(response);
    }
}