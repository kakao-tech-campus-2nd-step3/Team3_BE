package com.splanet.splanet.subscription.controller;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.core.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/subscription/me")
@Tag(name = "Subscription", description = "구독 관련 API")
public interface SubscriptionApi {

    @GetMapping
    @Operation(summary = "구독 조회", description = "사용자의 구독 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 정보가 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "활성화된 구독을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<SubscriptionResponse> getSubscription(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @DeleteMapping
    @Operation(summary = "구독 취소", description = "사용자의 구독을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독이 성공적으로 취소되었습니다."),
            @ApiResponse(responseCode = "400", description = "이미 취소된 구독입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> cancelSubscription(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @PostMapping("/payment")
    @Operation(summary = "구독", description = "사용자가 구독을 구매합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독이 성공적으로 구매되었습니다.",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class)))
    })
    ResponseEntity<SubscriptionResponse> subscribe(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "구독 결제 요청 정보", required = true) @RequestBody SubscriptionRequest request);
}
