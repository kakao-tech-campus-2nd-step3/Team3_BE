package com.splanet.splanet.Subscription.controller;

import com.splanet.splanet.Subscription.dto.SubscriptionDto;
import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.Subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription/me")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // 구독 조회
    @GetMapping
    public ResponseEntity<SubscriptionDto> getSubscription(@RequestParam Long userId) {
        return subscriptionService.getSubscription(userId);
    }

    // 구독 취소
    @DeleteMapping
    public ResponseEntity<String> cancelSubscription(@RequestParam Long userId) {
        return subscriptionService.cancelSubscription(userId);
    }

    // 구독하기
    @PostMapping("/payment")
    public ResponseEntity<SubscriptionDto> subscribe(
            @RequestParam Long userId,
            @RequestParam Subscription.Type type) {

        return subscriptionService.subscribe(userId, type);
    }
}