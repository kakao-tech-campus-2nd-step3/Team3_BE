package com.splanet.splanet.plan.repository;

import com.splanet.splanet.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByUserId(Long userId);
    List<Plan> findAllByUserIdAndAccessibility(Long userId, Boolean accessibility);
}
