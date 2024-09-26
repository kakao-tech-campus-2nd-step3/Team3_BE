package com.splanet.splanet.Subscription.dto;

import com.splanet.splanet.Subscription.entity.Subscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionDto {
    private Long id;
    private Long userId;
    private String subscriptionType;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private String message;

    public static SubscriptionDto fromSubscription(Subscription subscription) {
        return SubscriptionDto.builder()
                .subscriptionType(subscription.getType().name().toLowerCase())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus().name().toLowerCase())
                .build();
    }

    public static SubscriptionDto withMessageAndDetails(String message, Subscription subscription) {
        return SubscriptionDto.builder()
                .message(message)
                .id(subscription.getId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .build();
    }
}