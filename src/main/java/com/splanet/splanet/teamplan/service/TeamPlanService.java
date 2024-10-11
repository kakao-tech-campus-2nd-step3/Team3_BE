package com.splanet.splanet.teamplan.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.plan.entity.TeamPlan;
import com.splanet.splanet.plan.mapper.TeamPlanMapper;
import com.splanet.splanet.plan.repository.TeamPlanRepository;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
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

    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

    TeamPlan teamPlan = teamPlanMapper.toEntity(requestDto, team);
    teamPlanRepository.save(teamPlan);
    return teamPlanMapper.toResponseDto(teamPlan);
  }

  // 팀 플랜 조회
  @Transactional(readOnly = true)
  public TeamPlanResponseDto getTeamPlan(Long teamId, Long planId) {
    TeamPlan teamPlan = teamPlanRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    if (!teamPlan.getTeam().getId().equals(teamId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    return teamPlanMapper.toResponseDto(teamPlan);
  }

  // 팀에 속한 모든 플랜 조회
  @Transactional(readOnly = true)
  public List<TeamPlanResponseDto> getAllTeamPlans(Long teamId) {
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

    teamPlan.updatePlan(
            requestDto.getTitle(),
            requestDto.getDescription(),
            requestDto.getStartDate(),
            requestDto.getEndDate(),
            requestDto.getAccessibility(),
            requestDto.getIsCompleted()
    );

    teamPlanRepository.save(teamPlan);
    return teamPlanMapper.toResponseDto(teamPlan);
  }

  // 팀 플랜 삭제
  @Transactional
  public void deleteTeamPlan(Long userId, Long teamId, Long planId) {
    validateAdmin(teamId, userId);

    TeamPlan teamPlan = teamPlanRepository.findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));
    teamPlanRepository.delete(teamPlan);
  }
}