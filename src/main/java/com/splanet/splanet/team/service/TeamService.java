package com.splanet.splanet.team.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.entity.*;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
  public Team createTeam(String teamName, Long userId) {
    if (teamName == null || teamName.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    User user = findUserById(userId);
    Team team = Team.builder()
            .teamName(teamName)
            .user(user)
            .build();

    teamRepository.save(team);

    TeamUserRelation teamUserRelation = new TeamUserRelation(team, user, UserTeamRole.ADMIN);
    teamUserRelationRepository.save(teamUserRelation);

    return team;
  }
  @Transactional
  public Team addUserToTeam(Long teamId, Long userId) {
    Team team = findTeamById(teamId);
    User user = findUserById(userId);

    TeamUserRelation teamUserRelation = new TeamUserRelation(team, user, UserTeamRole.MEMBER);
    teamUserRelationRepository.save(teamUserRelation);

    return team;
  }

  @Transactional
  public void promoteUserToAdmin(Long teamId, Long userId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);
    User userToPromote = findUserById(userId);

    TeamUserRelation adminRelation = findTeamUserRelation(team, adminUser);

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    TeamUserRelation teamUserRelation = findTeamUserRelation(team, userToPromote);
    teamUserRelation.promoteToAdmin();
    teamUserRelationRepository.save(teamUserRelation);
  }

  @Transactional
  public void removeUserFromTeam(Long teamId, Long userId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);
    User userToRemove = findUserById(userId);

    TeamUserRelation adminRelation = findTeamUserRelation(team, adminUser);

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    TeamUserRelation relation = findTeamUserRelation(team, userToRemove);
    teamUserRelationRepository.delete(relation);
  }

  @Transactional
  public Team addUserToTeamByNickname(Long teamId, String nickname, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);

    TeamUserRelation adminRelation = findTeamUserRelation(team, adminUser);

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    User userToAdd = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    TeamUserRelation teamUserRelation = new TeamUserRelation(team, userToAdd, UserTeamRole.MEMBER);
    teamUserRelationRepository.save(teamUserRelation);

    return team;
  }

  @Transactional(readOnly = true)
  public List<User> getTeamMembers(Long teamId, Long userId) {
    Team team = findTeamById(teamId);
    User user = findUserById(userId);

    findTeamUserRelation(team, user);

    List<TeamUserRelation> teamUserRelations = teamUserRelationRepository.findAllByTeam(team);
    return teamUserRelations.stream()
            .map(TeamUserRelation::getUser)
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
  public void inviteUserToTeamByNickname(Long teamId, Long adminId, String nickname) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);

    // 관리자 권한 검증
    TeamUserRelation adminRelation = findTeamUserRelation(team, adminUser);
    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // 초대할 사용자를 닉네임으로 찾기
    User userToInvite = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 초대할 사용자가 이미 팀에 있는지 확인
    boolean isUserInTeam = teamUserRelationRepository.findByTeamAndUser(team, userToInvite).isPresent();
    if (isUserInTeam) {
      throw new BusinessException(ErrorCode.USER_ALREADY_IN_TEAM);
    }

    // 팀에 없는 사용자를 초대함
    TeamInvitation invitation = new TeamInvitation(team, userToInvite);
    teamInvitationRepository.save(invitation);
  }

  @Transactional
  public void acceptTeamInvitation(Long invitationId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
    }

    invitation.accept();
    teamInvitationRepository.save(invitation);

    TeamUserRelation teamUserRelation = new TeamUserRelation(invitation.getTeam(), invitation.getUser(), UserTeamRole.MEMBER);
    teamUserRelationRepository.save(teamUserRelation);
  }
  @Transactional
  public void rejectTeamInvitation(Long invitationId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
    }

    invitation.reject();
    teamInvitationRepository.save(invitation);
  }

}