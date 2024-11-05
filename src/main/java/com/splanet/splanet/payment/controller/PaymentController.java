package com.splanet.splanet.payment.controller;

import com.splanet.splanet.payment.dto.PaymentRequest;
import com.splanet.splanet.payment.dto.PaymentResponse;
import com.splanet.splanet.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<PaymentResponse> getPaymentStatus(Long paymentId) {
        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createPayment(Long userId, PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(userId, request.subscriptionId(), request.price());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "결제가 성공적으로 생성되었습니다.",
                "payment", response
        ));
    }

    @Override
    public ResponseEntity<Map<String, String>> deletePayment(Long userId, Long paymentId) {
        paymentService.deletePayment(userId, paymentId);
        return ResponseEntity.ok().body(Map.of("message", "결제가 성공적으로 삭제되었습니다."));
    }
}
