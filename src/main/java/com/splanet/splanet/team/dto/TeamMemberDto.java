package com.splanet.splanet.team.dto;

import lombok.Getter;

@Getter
public class TeamMemberDto {
  private Long userId;
  private String nickname;
  private String profileImage;

  public TeamMemberDto(Long userId, String nickname, String profileImage) {
    this.userId = userId;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }
}
