//package com.splanet.splanet.team.service;
//
//import com.splanet.splanet.core.exception.BusinessException;
//import com.splanet.splanet.core.exception.ErrorCode;
//import com.splanet.splanet.team.dto.TeamDto;
//import com.splanet.splanet.team.dto.TeamInvitationDto;
//import com.splanet.splanet.team.entity.*;
//import com.splanet.splanet.team.repository.TeamInvitationRepository;
//import com.splanet.splanet.team.repository.TeamRepository;
//import com.splanet.splanet.team.repository.TeamUserRelationRepository;
//import com.splanet.splanet.user.dto.UserDto;
//import com.splanet.splanet.user.entity.User;
//import com.splanet.splanet.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class TeamServiceTest {
//  @Mock
//  private TeamRepository teamRepository;
//
//  @Mock
//  private UserRepository userRepository;
//
//  @Mock
//  private TeamUserRelationRepository teamUserRelationRepository;
//
//  @Mock
//  private TeamInvitationRepository teamInvitationRepository;
//
//
//  @InjectMocks
//  private TeamService teamService;
//
//  private User user;
//  private User user2;
//  private Team team;
//  private Team team1;
//  private Team team2;
//  private TeamInvitation invitation1;
//  private TeamInvitation invitation2;
//
//  @BeforeEach
//  void setUp() {
//    user = User.builder()
//            .id(1L)
//            .nickname("TestUser")
//            .build();
//    user2 = User.builder()
//            .id(2L)
//            .nickname("TestUser2")
//            .build();
//
//    team = Team.builder()
//            .teamName("TestTeam")
//            .user(user)
//            .build();
//    team1 = Team.builder()
//            .id(1L)
//            .teamName("Team 1")
//            .user(user)
//            .build();
//
//    team2 = Team.builder()
//            .id(2L)
//            .teamName("Team 2")
//            .user(user)
//            .build();
//
//    invitation1 = new TeamInvitation(team1, user);
//    invitation1.accept();
//    invitation2 = new TeamInvitation(team2, user);
//  }
//
//  @Test
//  void createTeam() {
//    // given
//    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
//    when(teamRepository.save(any(Team.class))).thenReturn(team);
//
//    // when
//    TeamDto teamDto = teamService.createTeam("TestTeam", 1L);
//
//    // then
//    assertNotNull(teamDto);
//    assertEquals("TestTeam", teamDto.getTeamName());
//    assertEquals(user.getId(), teamDto.getUser().getId());
//    verify(teamRepository, times(1)).save(any(Team.class));
//    verify(teamUserRelationRepository, times(1)).save(any(TeamUserRelation.class));
//  }
//
//  @Test
//  void createTeam_InvalidInput_ThrowsException() {
//    // given
//    String invalidTeamName = "";
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.createTeam(invalidTeamName, 1L);
//    });
//
//    assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());
//    verify(teamRepository, never()).save(any(Team.class));
//  }
//
//  @Test
//  void createTeam_UserNotFound_ThrowsException() {
//    // given
//    when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.createTeam("TestTeam", 1L);
//    });
//
//    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//    verify(teamRepository, never()).save(any(Team.class));
//  }
//
//  @Test
//  void inviteUserToTeamByNickname() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(java.util.Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
//    when(teamUserRelationRepository.findByTeamAndUser(team, user))
//            .thenReturn(java.util.Optional.of(new TeamUserRelation(team, user, UserTeamRole.ADMIN)));
//    when(userRepository.findByNickname("TestUser2")).thenReturn(java.util.Optional.of(user2));
//    when(teamUserRelationRepository.findByTeamAndUser(team, user2)).thenReturn(java.util.Optional.empty());
//
//    // when
//    TeamInvitationDto invitationDto = teamService.inviteUserToTeamByNickname(1L, 1L, "TestUser2");
//
//    // then
//    assertNotNull(invitationDto);
//    assertEquals("TestTeam", invitationDto.getTeamName());
//    assertEquals(InvitationStatus.PENDING, invitationDto.getStatus());
//    verify(teamInvitationRepository, times(1)).save(any(TeamInvitation.class));
//  }
//
//  @Test
//  void getUserPendingInvitations() {
//    // given
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(teamInvitationRepository.findAllByUserAndStatus(user, InvitationStatus.PENDING))
//            .thenReturn(Arrays.asList(invitation2)); // Only pending invitations are returned
//
//    // when
//    List<TeamInvitationDto> pendingInvitations = teamService.getUserPendingInvitations(1L);
//
//    // then
//    assertNotNull(pendingInvitations);
//    assertEquals(1, pendingInvitations.size());
//    assertEquals("Team 2", pendingInvitations.get(0).getTeamName());
//    assertEquals(InvitationStatus.PENDING, pendingInvitations.get(0).getStatus());
//
//    verify(userRepository, times(1)).findById(1L);
//    verify(teamInvitationRepository, times(1)).findAllByUserAndStatus(user, InvitationStatus.PENDING);
//  }
//
//  @Test
//  void getUserPendingInvitations_UserNotFound_ThrowsException() {
//    // given
//    when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.getUserPendingInvitations(1L);
//    });
//
//    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).findAllByUserAndStatus(any(), any());
//  }
//
//
//  @Test
//  void getUserPendingInvitations_NoPendingInvitations_ReturnsEmptyList() {
//    // given
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(teamInvitationRepository.findAllByUserAndStatus(user, InvitationStatus.PENDING))
//            .thenReturn(Arrays.asList()); // No pending invitations
//
//    // when
//    List<TeamInvitationDto> pendingInvitations = teamService.getUserPendingInvitations(1L);
//
//    // then
//    assertNotNull(pendingInvitations);
//    assertTrue(pendingInvitations.isEmpty());
//
//    verify(userRepository, times(1)).findById(1L);
//    verify(teamInvitationRepository, times(1)).findAllByUserAndStatus(user, InvitationStatus.PENDING);
//  }
//
//  @Test
//  void acceptTeamInvitation() {
//    // given
//    TeamInvitation invitation = new TeamInvitation(team, user); // ID는 자동 할당
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//    when(teamUserRelationRepository.save(any(TeamUserRelation.class))).thenReturn(any());
//
//    // when
//    teamService.acceptTeamInvitation(1L, 1L);
//
//    // then
//    assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
//    verify(teamInvitationRepository, times(1)).save(invitation);
//    verify(teamUserRelationRepository, times(1)).save(any(TeamUserRelation.class));
//  }
//
//  @Test
//  void acceptTeamInvitation_InvitationNotFound_ThrowsException() {
//    // given
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.acceptTeamInvitation(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.INVITATION_NOT_FOUND, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void acceptTeamInvitation_AccessDenied_ThrowsException() {
//    // given
//    User anotherUser = User.builder().id(2L).nickname("AnotherUser").build();
//    TeamInvitation invitation = new TeamInvitation(team, anotherUser);
//
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.acceptTeamInvitation(1L, 1L); // 유저 ID가 초대 유저와 일치하지 않음
//    });
//
//    assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void acceptTeamInvitation_InvitationAlreadyProcessed_ThrowsException() {
//    // given
//    TeamInvitation invitation = new TeamInvitation(team, user);
//    invitation.accept(); // 초대가 이미 처리됨
//
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.acceptTeamInvitation(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.INVITATION_ALREADY_PROCESSED, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void rejectTeamInvitation() {
//    // given
//    TeamInvitation invitation = new TeamInvitation(team, user);
//
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//
//    // when
//    teamService.rejectTeamInvitation(1L, 1L);
//
//    // then
//    assertEquals(InvitationStatus.REJECTED, invitation.getStatus());
//    verify(teamInvitationRepository, times(1)).save(invitation);
//  }
//
//  @Test
//  void rejectTeamInvitation_InvitationNotFound_ThrowsException() {
//    // given
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.rejectTeamInvitation(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.INVITATION_NOT_FOUND, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void rejectTeamInvitation_AccessDenied_ThrowsException() {
//    // given
//    User anotherUser = User.builder().id(2L).nickname("AnotherUser").build();
//    TeamInvitation invitation = new TeamInvitation(team, anotherUser);
//
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.rejectTeamInvitation(1L, 1L); // 유저 ID가 초대 유저와 일치하지 않음
//    });
//
//    assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void rejectTeamInvitation_InvitationAlreadyProcessed_ThrowsException() {
//    // given
//    TeamInvitation invitation = new TeamInvitation(team, user);
//    invitation.reject(); // 초대가 이미 처리됨
//
//    when(teamInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.rejectTeamInvitation(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.INVITATION_ALREADY_PROCESSED, exception.getErrorCode());
//    verify(teamInvitationRepository, never()).save(any());
//  }
//
//  @Test
//  void getTeamMembers() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(teamUserRelationRepository.findByTeamAndUser(team, user)).thenReturn(Optional.of(new TeamUserRelation(team, user, UserTeamRole.ADMIN)));
//
//    User member1 = User.builder().id(2L).nickname("Member1").build();
//    User member2 = User.builder().id(3L).nickname("Member2").build();
//
//    TeamUserRelation relation1 = new TeamUserRelation(team, member1, UserTeamRole.MEMBER);
//    TeamUserRelation relation2 = new TeamUserRelation(team, member2, UserTeamRole.MEMBER);
//
//    when(teamUserRelationRepository.findAllByTeam(team)).thenReturn(Arrays.asList(relation1, relation2));
//
//    // when
//    List<UserDto> teamMembers = teamService.getTeamMembers(1L, 1L);
//
//    // then
//    assertNotNull(teamMembers);
//    assertEquals(2, teamMembers.size());
//    assertEquals("Member1", teamMembers.get(0).getNickname());
//    assertEquals("Member2", teamMembers.get(1).getNickname());
//
//    verify(teamRepository, times(1)).findById(1L);
//    verify(userRepository, times(1)).findById(1L);
//    verify(teamUserRelationRepository, times(1)).findAllByTeam(team);
//  }
//
//  @Test
//  void getTeamMembers_TeamNotFound_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.getTeamMembers(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).findAllByTeam(any());
//  }
//
//  @Test
//  void getTeamMembers_UserNotFound_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.getTeamMembers(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).findAllByTeam(any());
//  }
//
//  @Test
//  void getTeamMembers_AccessDenied_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(teamUserRelationRepository.findByTeamAndUser(team, user)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.getTeamMembers(1L, 1L);
//    });
//
//    assertEquals(ErrorCode.TEAM_MEMBER_NOT_FOUND, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).findAllByTeam(any());
//  }
//
//  @Test
//  void promoteUserToAdmin() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Admin user
//    when(userRepository.findById(2L)).thenReturn(Optional.of(user2)); // User to be promoted
//    when(teamUserRelationRepository.findByTeamAndUser(team, user)).thenReturn(Optional.of(new TeamUserRelation(team, user, UserTeamRole.ADMIN)));
//    when(teamUserRelationRepository.findByTeamAndUser(team, user2)).thenReturn(Optional.of(new TeamUserRelation(team, user2, UserTeamRole.MEMBER)));
//
//    // when
//    teamService.promoteUserToAdmin(1L, 2L, 1L);
//
//    // then
//    verify(teamUserRelationRepository, times(1)).save(any(TeamUserRelation.class));
//  }
//
//  @Test
//  void promoteUserToAdmin_TeamNotFound_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.empty());
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.promoteUserToAdmin(1L, 2L, 1L);
//    });
//
//    assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).save(any());
//  }
//
//  @Test
//  void promoteUserToAdmin_UserNotFound_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Admin user
//    when(userRepository.findById(2L)).thenReturn(Optional.empty()); // User to be promoted
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.promoteUserToAdmin(1L, 2L, 1L);
//    });
//
//    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).save(any());
//  }
//
//  @Test
//  void promoteUserToAdmin_AccessDenied_ThrowsException() {
//    // given
//    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Admin user
//    when(userRepository.findById(2L)).thenReturn(Optional.of(user2)); // User to be promoted
//    when(teamUserRelationRepository.findByTeamAndUser(team, user)).thenReturn(Optional.empty()); // Not an admin
//
//    // when & then
//    BusinessException exception = assertThrows(BusinessException.class, () -> {
//      teamService.promoteUserToAdmin(1L, 2L, 1L);
//    });
//
//    assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
//    verify(teamUserRelationRepository, never()).save(any());
//  }
//}