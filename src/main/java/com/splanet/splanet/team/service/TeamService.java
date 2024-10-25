package com.splanet.splanet.team.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.dto.TeamDto;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.dto.TeamMemberDto;
import com.splanet.splanet.team.entity.*;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.user.dto.UserDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  // 1. 팀 생성
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

  // 2. 팀 초대
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
    return new TeamInvitationDto(
            invitation.getId(),
            invitation.getTeam().getId(),
            invitation.getTeam().getTeamName(),
            userToInvite.getId(),
            userToInvite.getNickname(),
            userToInvite.getProfileImage(),
            invitation.getStatus()
    );
  }

  // 3. 초대 수락/거절 처리
  @Transactional
  public void handleInvitationResponse(Long invitationId, Long userId, boolean isAccepted) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    // 유저 본인인지 확인
    if (!invitation.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
    }

    // 초대 수락/거절
    if (isAccepted) {
      invitation.accept();
      TeamUserRelation teamUserRelation = new TeamUserRelation(invitation.getTeam(), invitation.getUser(), UserTeamRole.MEMBER);
      teamUserRelationRepository.save(teamUserRelation);
    } else {
      invitation.reject();
    }

    teamInvitationRepository.save(invitation);
  }

  // 4. 팀에서 유저 내보내기
  @Transactional
  public void kickUserFromTeam(Long teamId, Long userId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);
    User targetUser = findUserById(userId);

    // 관리자인지 확인
    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    TeamUserRelation userRelation = teamUserRelationRepository.findByTeamAndUser(team, targetUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

    teamUserRelationRepository.delete(userRelation);
  }

  @Transactional
  public Map<String, Object> updateUserRole(Long teamId, Long userId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);
    User userToPromote = findUserById(userId);

    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    TeamUserRelation userRelation = teamUserRelationRepository.findByTeamAndUser(team, userToPromote)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

    // 권한 수정
    if (userRelation.getRole() == UserTeamRole.ADMIN) {
      userRelation.demoteToMember();
    } else {
      userRelation.promoteToAdmin();
    }

    teamUserRelationRepository.save(userRelation);

    // 반환할 정보를 Map으로 준비
    Map<String, Object> result = new HashMap<>();
    result.put("userId", userToPromote.getId());
    result.put("role", userRelation.getRole().name());

    return result;  // 권한 변경 후 userId와 role 반환
  }

  // 6. 관리자가 보낸 초대 목록 조회
  @Transactional(readOnly = true)
  public List<TeamInvitationDto> getAdminPendingInvitations(Long teamId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);

    // 관리자 권한 확인
    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    List<TeamInvitation> invitations = teamInvitationRepository.findAllByTeamAndStatus(team, InvitationStatus.PENDING);
    return invitations.stream()
            .map(invitation -> new TeamInvitationDto(
                    invitation.getId(),
                    invitation.getTeam().getId(),
                    invitation.getTeam().getTeamName(),
                    invitation.getUser().getId(),
                    invitation.getUser().getNickname(),
                    invitation.getUser().getProfileImage(),
                    invitation.getStatus()))
            .collect(Collectors.toList());
  }

  // 7. 팀 초대 취소
  @Transactional
  public void cancelTeamInvitation(Long invitationId, Long adminId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

    User adminUser = findUserById(adminId);
    Team team = invitation.getTeam();

    // 관리자인지 확인
    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // 초대 취소 처리 (삭제)
    teamInvitationRepository.delete(invitation);
  }

  // 8. 유저가 속한 팀의 모든 멤버 조회
  @Transactional(readOnly = true)
  public List<TeamMemberDto> getTeamMembers(Long teamId) {
    // 팀에 속한 모든 사용자 관계 조회 (관리자 포함)
    List<TeamUserRelation> teamUserRelations = teamUserRelationRepository.findAllByTeamWithUser(teamId);

    // TeamUserRelation에서 사용자 정보를 가져와 TeamMemberDto로 변환
    return teamUserRelations.stream()
            .map(relation -> new TeamMemberDto(
                    relation.getUser().getId(),
                    relation.getUser().getNickname(),
                    relation.getUser().getProfileImage()))
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TeamInvitationDto> getUserPendingInvitations(Long userId) {
    User user = findUserById(userId);
    List<TeamInvitation> invitations = teamInvitationRepository.findAllByUserAndStatus(user, InvitationStatus.PENDING);

    return invitations.stream()
            .map(invitation -> new TeamInvitationDto(
                    invitation.getId(),
                    invitation.getTeam().getId(),
                    invitation.getTeam().getTeamName(),
                    invitation.getUser().getId(),
                    invitation.getUser().getNickname(),
                    invitation.getUser().getProfileImage(),
                    invitation.getStatus()))
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TeamDto> getUserTeams(Long userId) {
    User user = findUserById(userId);
    // 유저가 속한 팀 목록을 조회
    List<TeamUserRelation> teamUserRelations = teamUserRelationRepository.findAllByUser(user);

    return teamUserRelations.stream()
            .map(relation -> {
              Team team = relation.getTeam();

              return new TeamDto(
                      team.getId(),
                      team.getTeamName(),
                      new UserDto(user.getId(), user.getNickname())
              );
            })
            .collect(Collectors.toList());
  }

  @Transactional
  public void deleteTeam(Long teamId, Long adminId) {
    Team team = findTeamById(teamId);
    User adminUser = findUserById(adminId);

    // 관리자 권한 확인
    TeamUserRelation adminRelation = teamUserRelationRepository.findByTeamAndUser(team, adminUser)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

    if (adminRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // 팀 삭제 - 연관된 엔티티도 함께 삭제되도록 설정
    teamRepository.delete(team);
  }

  @Transactional
  public void leaveTeam(Long teamId, Long userId) {
    Team team = findTeamById(teamId);
    User user = findUserById(userId);

    // 팀 내 사용자 관계 확인
    TeamUserRelation teamUserRelation = teamUserRelationRepository.findByTeamAndUser(team, user)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

    // 팀에서 사용자 관계 삭제
    teamUserRelationRepository.delete(teamUserRelation);
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