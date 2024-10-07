package com.splanet.splanet.subscription.dto;

import lombok.Getter;
import lombok.Setter;
import com.splanet.splanet.subscription.entity.Subscription;

@Getter
@Setter
public class SubscriptionRequest {
    private Subscription.Type type;
}