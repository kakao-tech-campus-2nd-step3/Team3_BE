package com.splanet.splanet.subscription.dto;

import lombok.Getter;
import lombok.Setter;
import com.splanet.splanet.subscription.entity.Subscription;

@Getter
@Setter
public class SubscriptionResponse {
    private String message;
    private SubscriptionDetails subscription;

    public SubscriptionResponse(String message, Subscription subscription) {
        this.message = message;
        this.subscription = new SubscriptionDetails(subscription);
    }

    @Getter
    @Setter
    public static class SubscriptionDetails {
        private Long id;
        private String startDate;
        private String endDate;

        public SubscriptionDetails(Subscription subscription) {
            this.id = subscription.getId();
            this.startDate = subscription.getStartDate() != null ? subscription.getStartDate().toString() : null;
            this.endDate = subscription.getEndDate() != null ? subscription.getEndDate().toString() : null;
        }
    }
}