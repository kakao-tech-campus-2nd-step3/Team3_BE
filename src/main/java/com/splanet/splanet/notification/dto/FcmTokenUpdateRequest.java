package com.splanet.splanet.notification.dto;

public record FcmTokenUpdateRequest(String token, Boolean isNotificationEnabled, Integer notificationOffset) {}