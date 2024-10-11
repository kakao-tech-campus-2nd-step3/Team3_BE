package com.splanet.splanet.teamplan.mapper;

import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.team.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamPlanMapper {

  // TeamPlanRequestDto를 통해 TeamPlan 엔티티 생성
  public TeamPlan toEntity(TeamPlanRequestDto requestDto, Team team) {
    return TeamPlan.builder()
            .team(team)
            .title(requestDto.title())  // getter가 아니라 필드 이름을 사용
            .description(requestDto.description())  // getter가 아니라 필드 이름을 사용
            .startDate(requestDto.startDate())  // getter가 아니라 필드 이름을 사용
            .endDate(requestDto.endDate())  // getter가 아니라 필드 이름을 사용
            .accessibility(requestDto.accessibility())  // getter가 아니라 필드 이름을 사용
            .isCompleted(requestDto.isCompleted())  // getter가 아니라 필드 이름을 사용
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