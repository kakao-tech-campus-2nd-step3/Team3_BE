package com.splanet.splanet.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String nickname;
    private String profileImage;
    private Boolean isPremium;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
