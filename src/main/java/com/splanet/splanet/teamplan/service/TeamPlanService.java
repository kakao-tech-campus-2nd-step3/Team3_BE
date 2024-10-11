package com.splanet.splanet.teamplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.teamplan.mapper.TeamPlanMapper;
import com.splanet.splanet.teamplan.repository.TeamPlanRepository;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamPlanService {

  private final TeamPlanRepository teamPlanRepository;
  private final TeamRepository teamRepository;
  private final TeamUserRelationRepository teamUserRelationRepository;
  private final UserRepository userRepository;
  private final TeamPlanMapper teamPlanMapper;

  // 팀 관리자 권한 확인 메서드
  private void validateAdmin(Long teamId, Long userId) {
    TeamUserRelation teamUserRelation = teamUserRelationRepository.findByTeamIdAndUserId(teamId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));
    if (teamUserRelation.getRole() != UserTeamRole.ADMIN) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }
  }

  // 팀 플랜 생성
  @Transactional
  public TeamPlanResponseDto createTeamPlan(Long userId, Long teamId, TeamPlanRequestDto requestDto) {
    validateAdmin(teamId, userId);

    Team team = findTeamById(teamId);

    TeamPlan teamPlan = teamPlanMapper.toEntity(requestDto, team);
    teamPlanRepository.save(teamPlan);
    return teamPlanMapper.toResponseDto(teamPlan);
  }

  // 팀 플랜 조회
  @Transactional(readOnly = true)
  public TeamPlanResponseDto getTeamPlan(Long teamId, Long planId) {
    // 팀 존재 여부 확인
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

    // 팀 플랜 조회
    TeamPlan teamPlan = teamPlanRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    // 팀 플랜이 해당 팀에 속하는지 확인
    if (!teamPlan.getTeam().getId().equals(teamId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    return teamPlanMapper.toResponseDto(teamPlan);
  }

  // 팀에 속한 모든 플랜 조회
  @Transactional(readOnly = true)
  public List<TeamPlanResponseDto> getAllTeamPlans(Long teamId) {
    // 팀 존재 여부 확인
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

    // 팀에 속한 모든 플랜 조회
    List<TeamPlan> teamPlans = teamPlanRepository.findAllByTeamId(teamId);

    return teamPlans.stream()
            .map(teamPlanMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  // 팀 플랜 수정
  @Transactional
  public TeamPlanResponseDto updateTeamPlan(Long userId, Long teamId, Long planId, TeamPlanRequestDto requestDto) {
    validateAdmin(teamId, userId);

    TeamPlan teamPlan = teamPlanRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    // 빌더 패턴을 사용해 TeamPlan 객체 업데이트
    TeamPlan updatedPlan = teamPlan.toBuilder()
            .title(requestDto.title())
            .description(requestDto.description())
            .startDate(requestDto.startDate())
            .endDate(requestDto.endDate())
            .accessibility(requestDto.accessibility())
            .isCompleted(requestDto.isCompleted())
            .build();

    teamPlanRepository.save(updatedPlan);
    return teamPlanMapper.toResponseDto(updatedPlan);
  }

  // 팀 플랜 삭제
  @Transactional
  public void deleteTeamPlan(Long userId, Long teamId, Long planId) {
    validateAdmin(teamId, userId);

    TeamPlan teamPlan = teamPlanRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    teamPlanRepository.delete(teamPlan);
  }

  // 팀 찾기 (도움 메서드)
  private Team findTeamById(Long teamId) {
    return teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
  }

  // 유저 찾기 (도움 메서드)
  private User findUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }
}