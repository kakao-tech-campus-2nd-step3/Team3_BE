package com.splanet.splanet.Subscription.controller;

import com.splanet.splanet.Subscription.dto.SubscriptionDto;
import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.Subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription/me")
@Tag(name = "subscription", description = "구독 관련 API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    @Operation(summary = "구독 조회", description = "사용자의 구독 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 정보가 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "404", description = "활성화된 구독을 찾을 수 없습니다.")
    })
    public ResponseEntity<SubscriptionDto> getSubscription(@RequestParam Long userId) {
        return subscriptionService.getSubscription(userId);
    }

    @DeleteMapping
    @Operation(summary = "구독 취소", description = "사용자의 구독을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독이 성공적으로 취소되었습니다."),
            @ApiResponse(responseCode = "400", description = "이미 취소된 구독입니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    public ResponseEntity<String> cancelSubscription(@RequestParam Long userId) {
        return subscriptionService.cancelSubscription(userId);
    }

    @PostMapping("/payment")
    @Operation(summary = "구독", description = "사용자가 구독을 구매합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독이 성공적으로 구매되었습니다.")
    })
    public ResponseEntity<SubscriptionDto> subscribe(
            @RequestParam Long userId,
            @RequestParam Subscription.Type type) {

        return subscriptionService.subscribe(userId, type);
    }
}