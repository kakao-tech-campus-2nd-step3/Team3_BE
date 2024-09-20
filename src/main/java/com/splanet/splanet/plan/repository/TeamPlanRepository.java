package com.splanet.splanet.plan.repository;

import com.splanet.splanet.plan.entity.TeamPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamPlanRepository extends JpaRepository<TeamPlan, Long> {
}
