package com.splanet.splanet.payment.controller;

import com.splanet.splanet.core.exception.ErrorResponse;
import com.splanet.splanet.payment.dto.PaymentRequest;
import com.splanet.splanet.payment.dto.PaymentResponse;
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

import java.util.Map;

@RequestMapping("/api/payment")
@Tag(name = "Payments", description = "결제 관련 API")
public interface PaymentApi {

    @PostMapping
    @Operation(summary = "결제 생성", description = "새로운 구독 결제를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "결제가 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 구독 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 결제 처리에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Map<String, Object>> createPayment(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "결제 요청 정보", required = true) @RequestBody PaymentRequest request);

    @DeleteMapping("/{paymentId}")
    @Operation(summary = "결제 삭제", description = "특정 결제 상태를 삭제 합니다.")
    ResponseEntity<Map<String, String>> deletePayment(
            @Parameter(description = "결제 ID", required = true) @PathVariable Long paymentId);

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 상태 조회", description = "결제 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 정보가 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 결제 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PaymentResponse> getPaymentStatus(
            @Parameter(description = "결제 ID", required = true) @PathVariable Long paymentId);
}
