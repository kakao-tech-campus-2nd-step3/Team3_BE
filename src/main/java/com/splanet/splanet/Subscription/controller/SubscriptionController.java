package com.splanet.splanet.Subscription.controller;

import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.Subscription.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscription/me")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // 구독 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSubscription(@RequestParam Long userId) {
        return subscriptionService.getSubscription(userId);
    }

    // 구독 취소
    @DeleteMapping
    public ResponseEntity<String> cancelSubscription(@RequestParam Long userId) {
        subscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok("구독이 성공적으로 취소되었습니다.");
    }

    // 구독하기
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> subscribe(
            @RequestParam Long userId,
            @RequestParam Subscription.Type type) {

        Map<String, Object> createdSubscription = subscriptionService.subscribe(userId, type);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubscription);
    }
}