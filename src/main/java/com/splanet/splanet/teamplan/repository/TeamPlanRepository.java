package com.splanet.splanet.teamplan.repository;

import com.splanet.splanet.teamplan.entity.TeamPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPlanRepository extends JpaRepository<TeamPlan, Long> {
  List<TeamPlan> findAllByTeamId(Long teamId);
}
