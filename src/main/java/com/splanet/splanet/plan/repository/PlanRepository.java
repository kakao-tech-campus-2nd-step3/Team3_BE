package com.splanet.splanet.plan.repository;

import com.splanet.splanet.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByUserId(Long userId);
    List<Plan> findAllByUserIdAndAccessibility(Long userId, Boolean accessibility);
    List<Plan> findAllByUserIdAndStartDateAfter(Long userId, LocalDateTime currentDateTime);

    @Query("SELECT p FROM Plan p JOIN FETCH p.user WHERE p.startDate > :now AND p.isCompleted = false")
    List<Plan> findUpcomingPlans(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Plan p WHERE p.user.id = :userId AND p.startDate > :currentTime")
    List<Plan> findAllFuturePlansByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

}