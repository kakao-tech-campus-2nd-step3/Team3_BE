package com.splanet.splanet.payment.controller;

import com.splanet.splanet.payment.dto.PaymentDto;
import com.splanet.splanet.payment.service.PaymentService;
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

    // 결제 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentDto.CreateRequest request) {

        PaymentDto.Response response = paymentService.createPayment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "결제가 성공적으로 생성되었습니다.",
                "payment", response
        ));
    }

    // 결제 삭제
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Map<String, String>> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok().body(Map.of("message", "결제가 성공적으로 삭제되었습니다."));
    }

    // 결제 상태 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto.Response> getPaymentStatus(@PathVariable Long paymentId) {
        PaymentDto.Response response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }
}