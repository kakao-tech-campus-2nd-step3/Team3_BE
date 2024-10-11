package com.splanet.splanet.team.dto;

import com.splanet.splanet.team.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamInvitationDto {
  private Long invitationId;
  private Long teamId;
  private String teamName;
  private Long userId;
  private String nickname;
  private String profileImage;
  private InvitationStatus status;
}