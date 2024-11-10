package com.splanet.splanet.payment.dto;

public record PaymentRequest(Long subscriptionId, int price) {
}