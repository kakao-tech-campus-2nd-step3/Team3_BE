package com.splanet.splanet.payment.dto;

import com.splanet.splanet.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long subscriptionId,
        int price,
        String status,
        LocalDateTime paymentDate,
        LocalDateTime createdAt
) {
    public static PaymentResponse fromPayment(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getSubscription().getId(),
                payment.getPrice(),
                payment.getStatus().name().toLowerCase(),
                payment.getPaymentDate(),
                payment.getCreatedAt()
        );
    }
}