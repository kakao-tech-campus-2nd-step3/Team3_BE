package com.splanet.splanet.plan.controller;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController implements PlanApi {

    private final PlanService planService;

    @Override
    public ResponseEntity<PlanResponseDto> createPlan(@AuthenticationPrincipal Long userId, @RequestBody PlanRequestDto requestDto) {
        PlanResponseDto responseDto = planService.createPlan(userId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<PlanResponseDto> getPlan(@PathVariable Long planId) {
        PlanResponseDto responseDto = planService.getPlanById(planId);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<List<PlanResponseDto>> getAllPlans(@AuthenticationPrincipal Long userId) {
        List<PlanResponseDto> plans = planService.getAllPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @Override
    public ResponseEntity<PlanResponseDto> updatePlan(@PathVariable Long planId, @RequestBody PlanRequestDto requestDto) {
        PlanResponseDto updatedPlan = planService.updatePlan(planId, requestDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @Override
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }
}
