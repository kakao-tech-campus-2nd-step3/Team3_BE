package com.splanet.splanet.subscription.controller;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionApi {

    private final SubscriptionService subscriptionService;

    @Override
    public ResponseEntity<SubscriptionResponse> getSubscription(Long userId) {
        return subscriptionService.getSubscription(userId);
    }

    @Override
    public ResponseEntity<String> cancelSubscription(Long userId) {
        return subscriptionService.cancelSubscription(userId);
    }

    @Override
    public ResponseEntity<SubscriptionResponse> subscribe(Long userId, SubscriptionRequest request) {
        return subscriptionService.subscribe(userId, request);
    }
}
