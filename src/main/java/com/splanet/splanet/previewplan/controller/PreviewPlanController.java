package com.splanet.splanet.previewplan.controller;

import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.dto.PlanGroupWithCardsResponseDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class PreviewPlanController implements PreviewPlanApi {

    private final PreviewPlanService previewPlanService;

    @Override
    public ResponseEntity<PlanCardResponseDto> createPlanCard(String deviceId, String groupId, PlanCardRequestDto planCardRequestDto) {
        PlanCardResponseDto responseDto = previewPlanService.savePlanCard(deviceId, groupId, planCardRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<PlanCardResponseDto> getPlanCard(String deviceId, String groupId, String cardId) {
        PlanCardResponseDto responseDto = previewPlanService.getPlanCard(deviceId, groupId, cardId);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<PlanCardResponseDto> updatePlanCard(String deviceId, String groupId, String cardId, PlanCardRequestDto planCardRequestDto) {
        PlanCardResponseDto responseDto = previewPlanService.updatePlanCard(deviceId, groupId, cardId, planCardRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<Void> deletePlanCard(String deviceId, String groupId, String cardId) {
        previewPlanService.deletePlanCard(deviceId, groupId, cardId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Set<PlanGroupWithCardsResponseDto>> getPreviewPlans(String deviceId) {
        Set<PlanGroupWithCardsResponseDto> previewPlans = previewPlanService.getPreviewPlans(deviceId);
        return ResponseEntity.ok(previewPlans);
    }
}
