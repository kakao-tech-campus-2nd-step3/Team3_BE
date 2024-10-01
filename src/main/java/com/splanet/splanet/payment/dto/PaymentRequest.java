package com.splanet.splanet.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    private Long subscriptionId;
    private int price;
}