package com.splanet.splanet.teamplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.teamplan.mapper.TeamPlanMapper;
import com.splanet.splanet.teamplan.repository.TeamPlanRepository;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamPlanServiceTest {

    @InjectMocks
    private TeamPlanService teamPlanService;

    @Mock
    private TeamPlanRepository teamPlanRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamUserRelationRepository teamUserRelationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamPlanMapper teamPlanMapper;

    private Team team;
    private User user;
    private TeamPlan teamPlan;
    private TeamPlanRequestDto teamPlanRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 가상 객체 생성
        user = mock(User.class);
        team = mock(Team.class);
        teamPlan = mock(TeamPlan.class);
        teamPlanRequestDto = mock(TeamPlanRequestDto.class);

        // 객체의 기본 동작 정의
        when(user.getId()).thenReturn(1L);
        when(team.getId()).thenReturn(1L);
        when(team.getTeamName()).thenReturn("Test Team");
        when(teamPlan.getId()).thenReturn(1L);
        when(teamPlan.getTitle()).thenReturn("Test Plan");
        when(teamPlan.getDescription()).thenReturn("Description");
        when(teamPlan.getStartDate()).thenReturn(LocalDateTime.now());
        when(teamPlan.getEndDate()).thenReturn(LocalDateTime.now().plusMonths(1));

        // teamRepository Mock 설정 (team.getId()에 해당하는 팀을 반환하도록 설정)
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        // 가상 요청 DTO 객체 설정
        when(teamPlanRequestDto.title()).thenReturn("Updated Plan");
        when(teamPlanRequestDto.description()).thenReturn("Updated Description");
        when(teamPlanRequestDto.startDate()).thenReturn(LocalDateTime.now());
        when(teamPlanRequestDto.endDate()).thenReturn(LocalDateTime.now().plusMonths(1));
        when(teamPlanRequestDto.accessibility()).thenReturn(true);
    }

    @Test
    void 팀_플랜_생성_성공() {
        // Given
        TeamUserRelation adminRelation = mock(TeamUserRelation.class);
        when(teamUserRelationRepository.findByTeamIdAndUserId(team.getId(), user.getId()))
                .thenReturn(Optional.of(adminRelation));
        when(adminRelation.getRole()).thenReturn(UserTeamRole.ADMIN);
        when(teamPlanMapper.toEntity(any(TeamPlanRequestDto.class), eq(team))).thenReturn(teamPlan);
        when(teamPlanRepository.save(any(TeamPlan.class))).thenReturn(teamPlan);
        when(teamPlanMapper.toResponseDto(any(TeamPlan.class))).thenReturn(mock(TeamPlanResponseDto.class));

        // When
        TeamPlanResponseDto response = teamPlanService.createTeamPlan(user.getId(), team.getId(), teamPlanRequestDto);

        // Then
        assertNotNull(response);
        verify(teamPlanRepository).save(any(TeamPlan.class));
    }

    @Test
    void 팀_플랜_조회_성공() {
        // Given
        when(teamPlan.getTeam()).thenReturn(team);
        when(team.getId()).thenReturn(1L);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(teamPlanRepository.findById(teamPlan.getId())).thenReturn(Optional.of(teamPlan));

        Long id = teamPlan.getId();
        String title = teamPlan.getTitle();
        String description = teamPlan.getDescription();
        LocalDateTime createdAt = teamPlan.getStartDate();
        LocalDateTime updatedAt = teamPlan.getEndDate();
        Boolean isActive = true;
        Boolean isDeleted = false;

        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime modifiedDate = LocalDateTime.now().plusDays(1);

        TeamPlanResponseDto responseDto = new TeamPlanResponseDto(id, title, description, createdAt, updatedAt, isActive, isDeleted, createdDate, modifiedDate);

        when(teamPlanMapper.toResponseDto(any(TeamPlan.class))).thenReturn(responseDto);

        // When
        TeamPlanResponseDto response = teamPlanService.getTeamPlan(team.getId(), teamPlan.getId());

        // Then
        assertNotNull(response);
        assertEquals(teamPlan.getTitle(), response.title());
        verify(teamPlanRepository).findById(teamPlan.getId());
    }

    @Test
    void 팀_플랜_삭제_실패_권한_없음() {
        // Given
        TeamUserRelation nonAdmin = mock(TeamUserRelation.class);
        when(nonAdmin.getUser()).thenReturn(mock(User.class));
        when(teamUserRelationRepository.findByTeamIdAndUserId(team.getId(), user.getId()))
                .thenReturn(Optional.of(nonAdmin));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> teamPlanService.deleteTeamPlan(user.getId(), team.getId(), teamPlan.getId()));
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    void 팀_플랜_삭제_성공() {
        // Given
        TeamUserRelation adminRelation = mock(TeamUserRelation.class);
        when(teamUserRelationRepository.findByTeamIdAndUserId(team.getId(), user.getId()))
                .thenReturn(Optional.of(adminRelation));
        when(adminRelation.getRole()).thenReturn(UserTeamRole.ADMIN);
        when(teamPlanRepository.findById(teamPlan.getId())).thenReturn(Optional.of(teamPlan));

        // When
        teamPlanService.deleteTeamPlan(user.getId(), team.getId(), teamPlan.getId());

        // Then
        verify(teamPlanRepository).delete(teamPlan);
    }
}