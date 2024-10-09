package com.splanet.splanet.team.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.dto.TeamDto;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.entity.*;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.user.dto.UserDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;
  private final TeamUserRelationRepository teamUserRelationRepository;
  private final TeamInvitationRepository teamInvitationRepository;

  public TeamService(TeamRepository teamRepository, UserRepository userRepository, TeamUserRelationRepository teamUserRelationRepository, TeamInvitationRepository teamInvitationRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
    this.teamUserRelationRepository = teamUserRelationRepository;
    this.teamInvitationRepository = teamInvitationRepository;
  }

  @Transactional
  public TeamDto createTeam(String teamName, Long userId) {
    if (teamName == null || teamName.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    User user = findUserById(userId);
    Team team = Team.builder()
            .teamName(teamName)
            .user(user)
            .build();

    teamRepository.save(team);

    // 생성한 유저를 관리자 역할로 팀에 추가
    TeamUserRelation teamUserRelation = new TeamUserRelation(team, user, UserTeamRole.ADMIN);
    teamUserRelationRepository.save(teamUserRelation);

    return new TeamDto(team.getId(), team.getTeamName(), new UserDto(user.getId(), user.getNickname()), null);
  }

  @Transactional
  public TeamInvitationDto inviteUserToTeamByNickname(Long teamId, Long adminId, String nickname) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);

    // 관리자 권한 확인
    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // 초대할 유저 찾기
    User userToInvite = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 이미 팀에 있는지 확인
    if (teamUserRelationRepository.findByTeamAndUser(team, userToInvite).isPresent()) {
      throw new BusinessException(ErrorCode.USER_ALREADY_IN_TEAM);
    }

    // 초대 저장
    TeamInvitation invitation = new TeamInvitation(team, userToInvite);
    teamInvitationRepository.save(invitation);

    // 초대 정보를 DTO로 반환
    return new TeamInvitationDto(invitation.getId(), invitation.getTeam().getId(), invitation.getTeam().getTeamName(), invitation.getStatus());
  }

  @Transactional(readOnly = true)
  public List<TeamInvitationDto> getUserPendingInvitations(Long userId) {
    User user = findUserById(userId);
    List<TeamInvitation> invitations = teamInvitationRepository.findAllByUserAndStatus(user, InvitationStatus.PENDING);
    return invitations.stream()
            .map(invitation -> new TeamInvitationDto(invitation.getId(), invitation.getTeam().getId(), invitation.getTeam().getTeamName(), invitation.getStatus()))
            .collect(Collectors.toList());
  }

  @Transactional
  public void acceptTeamInvitation(Long invitationId, Long userId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    // 유저 본인인지 확인
    if (!invitation.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
    }

    // 초대 수락 처리
    invitation.accept();
    teamInvitationRepository.save(invitation);

    // 팀 멤버 추가
    TeamUserRelation teamUserRelation = new TeamUserRelation(invitation.getTeam(), invitation.getUser(), UserTeamRole.MEMBER);
    teamUserRelationRepository.save(teamUserRelation);
  }

  @Transactional
  public void rejectTeamInvitation(Long invitationId, Long userId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    // 유저 본인인지 확인
    if (!invitation.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
    }

    // 초대 거절 처리
    invitation.reject();
    teamInvitationRepository.save(invitation);
  }

  @Transactional(readOnly = true)
  public List<UserDto> getTeamMembers(Long teamId, Long userId) {
    Team team = findTeamById(teamId);
    User user = findUserById(userId);

    findTeamUserRelation(team, user);

    List<TeamUserRelation> teamUserRelations = teamUserRelationRepository.findAllByTeam(team);
    return teamUserRelations.stream()
            .map(relation -> new UserDto(relation.getUser().getId(), relation.getUser().getNickname()))
            .collect(Collectors.toList());
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }

  private Team findTeamById(Long teamId) {
    return teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
  }

  private TeamUserRelation findTeamUserRelation(Team team, User user) {
    return teamUserRelationRepository.findByTeamAndUser(team, user)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));
  }
  @Transactional
  public void promoteUserToAdmin(Long teamId, Long userId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);
    User userToPromote = findUserById(userId);

    Optional<TeamUserRelation> adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser);
    if (adminRelation.isEmpty() || adminRelation.get().getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    TeamUserRelation teamUserRelation = findTeamUserRelation(team, userToPromote);

    teamUserRelation.promoteToAdmin();
    teamUserRelationRepository.save(teamUserRelation);
  }
}