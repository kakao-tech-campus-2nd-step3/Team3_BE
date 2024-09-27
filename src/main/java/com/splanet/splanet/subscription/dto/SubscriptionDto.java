package com.splanet.splanet.subscription.dto;

import com.splanet.splanet.subscription.entity.Subscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .subscriptionType(subscription.getType().name().toLowerCase())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus().name().toLowerCase())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .deleted(subscription.getDeleted())
                .build();
    }
}