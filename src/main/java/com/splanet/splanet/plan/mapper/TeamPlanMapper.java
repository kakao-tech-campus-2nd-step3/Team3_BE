package com.splanet.splanet.plan.mapper;

import com.splanet.splanet.plan.entity.TeamPlan;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import com.splanet.splanet.team.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamPlanMapper {

  // TeamPlanRequestDto를 통해 TeamPlan 엔티티 생성
  public TeamPlan toEntity(TeamPlanRequestDto requestDto, Team team) {
    return TeamPlan.builder()
            .team(team)
            .title(requestDto.getTitle())
            .description(requestDto.getDescription())
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .accessibility(requestDto.getAccessibility())
            .isCompleted(requestDto.getIsCompleted())
            .build();
  }

  // TeamPlan 엔티티를 TeamPlanResponseDto로 변환
  public TeamPlanResponseDto toResponseDto(TeamPlan teamPlan) {
    return TeamPlanResponseDto.builder()
            .id(teamPlan.getId())
            .title(teamPlan.getTitle())
            .description(teamPlan.getDescription())
            .startDate(teamPlan.getStartDate())
            .endDate(teamPlan.getEndDate())
            .accessibility(teamPlan.getAccessibility())
            .isCompleted(teamPlan.getIsCompleted())  // 완료 여부
            .createdAt(teamPlan.getCreatedAt())  // 생성일시
            .updatedAt(teamPlan.getUpdatedAt())  // 수정일시
            .build();
  }
}