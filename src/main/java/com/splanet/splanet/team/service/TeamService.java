package com.splanet.splanet.team.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
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

  public TeamService(TeamRepository teamRepository, UserRepository userRepository, TeamUserRelationRepository teamUserRelationRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
    this.teamUserRelationRepository = teamUserRelationRepository;
  }

  @Transactional
  public Team createTeam(String teamName, Long userId) {
    User user = findUserById(userId);

    Team team = Team.builder()
            .teamName(teamName)
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
}