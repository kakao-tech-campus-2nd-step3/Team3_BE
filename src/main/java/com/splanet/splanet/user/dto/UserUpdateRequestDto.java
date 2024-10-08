package com.splanet.splanet.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequestDto {
    private String nickname;
    private String profileImage;
    private Boolean isPremium;
}
