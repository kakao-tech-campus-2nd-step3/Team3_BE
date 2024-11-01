package com.splanet.splanet.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String token
) {}
