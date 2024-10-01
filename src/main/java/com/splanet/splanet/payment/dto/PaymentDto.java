package com.splanet.splanet.payment.dto;

import com.splanet.splanet.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PaymentDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private Long subscriptionId;
        private int price;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long subscription_id;
        private int price;
        private String status;
        private LocalDateTime paymentDate;
        private LocalDateTime createdAt;

        public static Response fromPayment(Payment payment) {
            return Response.builder()
                    .id(payment.getId())
                    .subscription_id(payment.getSubscription().getId())
                    .price(payment.getPrice())
                    .status(payment.getStatus().name().toLowerCase())
                    .paymentDate(payment.getPaymentDate())
                    .createdAt(payment.getCreatedAt())
                    .build();
        }
    }
}