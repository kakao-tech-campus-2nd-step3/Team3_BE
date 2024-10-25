package com.splanet.splanet.teamplan.mapper;

import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.team.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamPlanMapper {

  public TeamPlan toEntity(TeamPlanRequestDto requestDto, Team team) {
    return TeamPlan.builder()
            .team(team)
            .title(requestDto.title())
            .description(requestDto.description())
            .startDate(requestDto.startDate())
            .endDate(requestDto.endDate())
            .accessibility(requestDto.accessibility())
            .isCompleted(requestDto.isCompleted())
            .build();
  }

  // TeamPlan 엔티티를 TeamPlanResponseDto로 변환
  public TeamPlanResponseDto toResponseDto(TeamPlan teamPlan) {
    return new TeamPlanResponseDto(
            teamPlan.getId(),
            teamPlan.getTitle(),
            teamPlan.getDescription(),
            teamPlan.getStartDate(),
            teamPlan.getEndDate(),
            teamPlan.getAccessibility(),
            teamPlan.getIsCompleted(),
            teamPlan.getCreatedAt(),
            teamPlan.getUpdatedAt()
    );
  }
}