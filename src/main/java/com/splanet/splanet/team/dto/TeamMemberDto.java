package com.splanet.splanet.team.dto;

import lombok.Getter;

@Getter
public class TeamMemberDto {
  private Long userId;
  private String nickname;
  private String profileImage;
  private String role;

  public TeamMemberDto(Long userId, String nickname, String profileImage, String role) {
    this.userId = userId;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.role = role;
  }
}
