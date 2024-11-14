package com.splanet.splanet.team.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.dto.TeamDto;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.entity.*;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

  @InjectMocks
  private TeamService teamService;

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TeamUserRelationRepository teamUserRelationRepository;

  @Mock
  private TeamInvitationRepository teamInvitationRepository;

  private User testUser;
  private Team testTeam;
  private TeamUserRelation testRelation;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
            .id(1L)
            .nickname("testNickname")
            .profileImage("profileImage")
            .build();

    testTeam = Team.builder()
            .teamName("Test Team")
            .user(testUser)
            .build();

    testRelation = new TeamUserRelation(testTeam, testUser, UserTeamRole.ADMIN);

    lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    lenient().when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
  }

  @Test
  public void testCreateTeam_Success() {
    String teamName = "Test Team";

    TeamDto result = teamService.createTeam(teamName, 1L);

    assertNotNull(result);
    assertEquals(teamName, result.getTeamName());
    verify(teamRepository, times(1)).save(any(Team.class));
    verify(teamUserRelationRepository, times(1)).save(any(TeamUserRelation.class));
  }

  @Test
  public void testCreateTeam_InvalidInput() {
    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.createTeam("", 1L)
    );
    assertEquals(ErrorCode.TEAM_NAME_NOT_FOUND, exception.getErrorCode());
  }


  @Test
  public void testLeaveTeam_Success() {
    when(teamUserRelationRepository.findByTeamAndUser(any(Team.class), any(User.class)))
            .thenReturn(Optional.of(testRelation));

    teamService.leaveTeam(1L, 1L);

    verify(teamUserRelationRepository, times(1)).delete(testRelation);
  }

  @Test
  public void testLeaveTeam_UserNotInTeam() {
    when(teamUserRelationRepository.findByTeamAndUser(any(Team.class), any(User.class)))
            .thenReturn(Optional.empty());

    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.leaveTeam(1L, 1L)
    );

    assertEquals(ErrorCode.TEAM_MEMBER_NOT_FOUND, exception.getErrorCode());
  }


  @Test
  public void testInviteUserToTeam_Success() {
    // 관리자 권한 확인을 위해 ADMIN으로 설정
    when(teamUserRelationRepository.findByTeamAndUser(any(Team.class), any(User.class)))
            .thenReturn(Optional.of(testRelation)); // 관리자임을 설정

    User invitedUser = User.builder()
            .id(2L)
            .nickname("inviteUser")
            .profileImage("profileImage")
            .build();

    // 초대할 유저가 팀에 속해있지 않음을 설정
    when(userRepository.findByNickname(anyString())).thenReturn(Optional.of(invitedUser));
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, invitedUser)).thenReturn(Optional.empty());

    TeamInvitationDto result = teamService.inviteUserToTeamByNickname(1L, 1L, "inviteUser");

    assertNotNull(result);
    assertEquals("inviteUser", result.getNickname());
    verify(teamInvitationRepository, times(1)).save(any(TeamInvitation.class));
  }

  @Test
  public void testInviteUserToTeam_UserAlreadyInTeam() {
    // 관리자 권한 확인
    TeamUserRelation adminRelation = new TeamUserRelation(testTeam, testUser, UserTeamRole.ADMIN);
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, testUser)).thenReturn(Optional.of(adminRelation));

    // 이미 팀에 속한 유저를 설정
    User invitedUser = User.builder()
            .id(2L)
            .nickname("inviteUser")
            .profileImage("profileImage")
            .build();
    when(userRepository.findByNickname("inviteUser")).thenReturn(Optional.of(invitedUser));
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, invitedUser)).thenReturn(Optional.of(new TeamUserRelation(testTeam, invitedUser, UserTeamRole.MEMBER)));

    // USER_ALREADY_IN_TEAM 예외를 기대
    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.inviteUserToTeamByNickname(1L, 1L, "inviteUser")
    );

    assertEquals(ErrorCode.USER_ALREADY_IN_TEAM, exception.getErrorCode());
  }

  @Test
  public void testKickUserFromTeam_Success() {
    User targetUser = User.builder()
            .id(2L)
            .nickname("targetUser")
            .profileImage("profileImage")
            .build();

    when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, targetUser))
            .thenReturn(Optional.of(new TeamUserRelation(testTeam, targetUser, UserTeamRole.MEMBER)));
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, testUser))
            .thenReturn(Optional.of(testRelation));

    teamService.kickUserFromTeam(1L, 2L, 1L);

    verify(teamUserRelationRepository, times(1)).delete(any());
  }

  @Test
  public void testKickUserFromTeam_AccessDenied() {
    User targetUser = User.builder()
            .id(2L)
            .nickname("targetUser")
            .profileImage("profileImage")
            .build();

    when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, testUser))
            .thenReturn(Optional.of(new TeamUserRelation(testTeam, testUser, UserTeamRole.MEMBER)));

    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.kickUserFromTeam(1L, 2L, 1L)
    );

    assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
  }

  @Test
  public void testInviteUserToTeam_InvitationAlreadySent() {
    // 관리자 권한 확인을 위해 ADMIN으로 설정
    when(teamUserRelationRepository.findByTeamAndUser(any(Team.class), any(User.class)))
            .thenReturn(Optional.of(testRelation));

    User invitedUser = User.builder()
            .id(2L)
            .nickname("inviteUser")
            .profileImage("profileImage")
            .build();

    // 이미 보낸 초대가 PENDING 상태로 존재하는 상황을 설정
    TeamInvitation existingInvitation = new TeamInvitation(testTeam, invitedUser);
    when(userRepository.findByNickname(anyString())).thenReturn(Optional.of(invitedUser));
    when(teamInvitationRepository.findByTeamAndUserAndStatus(testTeam, invitedUser, InvitationStatus.PENDING))
            .thenReturn(Optional.of(existingInvitation));

    // 초대가 이미 존재하는 경우 예외를 기대
    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.inviteUserToTeamByNickname(1L, 1L, "inviteUser")
    );

    assertEquals(ErrorCode.INVITATION_ALREADY_SENT, exception.getErrorCode());
  }
  @Test
  public void testDeleteTeam_Success() {
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, testUser))
            .thenReturn(Optional.of(testRelation));

    // 팀 삭제 시 관련 데이터 삭제 확인
    doNothing().when(teamInvitationRepository).deleteAllByTeam(testTeam);
    doNothing().when(teamUserRelationRepository).deleteAllByTeam(testTeam);
    doNothing().when(teamRepository).delete(testTeam);

    teamService.deleteTeam(1L, 1L);

    verify(teamInvitationRepository, times(1)).deleteAllByTeam(testTeam);
    verify(teamUserRelationRepository, times(1)).deleteAllByTeam(testTeam);
    verify(teamRepository, times(1)).delete(testTeam);
  }

  @Test
  public void testDeleteTeam_AccessDenied() {
    TeamUserRelation memberRelation = new TeamUserRelation(testTeam, testUser, UserTeamRole.MEMBER);
    when(teamUserRelationRepository.findByTeamAndUser(testTeam, testUser))
            .thenReturn(Optional.of(memberRelation));

    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.deleteTeam(1L, 1L)
    );

    assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    verify(teamRepository, never()).delete(testTeam);
  }

  @Test
  public void testDeleteTeam_TeamNotFound() {
    when(teamRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException exception = assertThrows(BusinessException.class, () ->
            teamService.deleteTeam(1L, 1L)
    );

    assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    verify(teamInvitationRepository, never()).deleteAllByTeam(any());
    verify(teamUserRelationRepository, never()).deleteAllByTeam(any());
    verify(teamRepository, never()).delete(any());
  }

}