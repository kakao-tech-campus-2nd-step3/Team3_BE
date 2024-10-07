package com.splanet.splanet.payment.dto;

import com.splanet.splanet.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long subscriptionId;
    private int price;
    private String status;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

    public static PaymentResponse fromPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .subscriptionId(payment.getSubscription().getId())
                .price(payment.getPrice())
                .status(payment.getStatus().name().toLowerCase())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}