package com.splanet.splanet.payment.controller;

import com.splanet.splanet.core.exception.ErrorResponse;
import com.splanet.splanet.payment.dto.PaymentRequest;
import com.splanet.splanet.payment.dto.PaymentResponse;
import com.splanet.splanet.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "Payments", description = "결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 생성", description = "새로운 구독 결제를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결제가 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 구독 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 오류로 결제 처리에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> createPayment(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "결제가 성공적으로 생성되었습니다.",
                "payment", response
        ));
    }

    @DeleteMapping("/{paymentId}")
    @Operation(summary = "결제 삭제", description = "특정 결제 상태를 삭제 합니다.")
    public ResponseEntity<Map<String, String>> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok().body(Map.of("message", "결제가 성공적으로 삭제되었습니다."));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 상태 조회", description = "결제 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "결제 정보가 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 결제 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }
}