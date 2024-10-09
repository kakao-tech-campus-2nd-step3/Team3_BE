package com.splanet.splanet.team.controller;

import com.splanet.splanet.team.api.TeamApi;
import com.splanet.splanet.team.dto.TeamDto;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.service.TeamService;
import com.splanet.splanet.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TeamController implements TeamApi {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @Override
  public ResponseEntity<TeamDto> createTeam(String teamName, Long userId) {
    TeamDto createdTeam = teamService.createTeam(teamName, userId);
    return ResponseEntity.ok(createdTeam);
  }

  @Override
  public ResponseEntity<TeamInvitationDto> inviteUserToTeam(Long teamId, Long adminId, String nickname) {
    TeamInvitationDto invitation = teamService.inviteUserToTeamByNickname(teamId, adminId, nickname);
    return ResponseEntity.ok(invitation);
  }

  @Override
  public ResponseEntity<Void> handleInvitationResponse(Long invitationId, boolean isAccepted, Long userId) {
    teamService.handleInvitationResponse(invitationId, userId, isAccepted);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<TeamInvitationDto>> getUserPendingInvitations(Long userId) {
    List<TeamInvitationDto> invitations = teamService.getUserPendingInvitations(userId);
    return ResponseEntity.ok(invitations);
  }

  @Override
  public ResponseEntity<List<UserDto>> getTeamMembers(Long teamId, Long userId) {
    List<UserDto> members = teamService.getTeamMembers(teamId, userId);
    return ResponseEntity.ok(members);
  }

  @Override
  public ResponseEntity<Map<String, Object>> updateUserRole(Long teamId, Long userId, Long adminId) {
    Map<String, Object> result = teamService.updateUserRole(teamId, userId, adminId);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<List<TeamInvitationDto>> getAdminPendingInvitations(Long teamId, Long adminId) {
    List<TeamInvitationDto> invitations = teamService.getAdminPendingInvitations(teamId, adminId);
    return ResponseEntity.ok(invitations);
  }

  @Override
  public ResponseEntity<Void> cancelTeamInvitation(Long invitationId, Long adminId) {
    teamService.cancelTeamInvitation(invitationId, adminId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> kickUserFromTeam(Long teamId, Long userId, Long adminId) {
    teamService.kickUserFromTeam(teamId, userId, adminId);
    return ResponseEntity.noContent().build();
  }
}