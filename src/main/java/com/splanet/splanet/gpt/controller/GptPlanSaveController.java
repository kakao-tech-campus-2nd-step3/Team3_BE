package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.previewplan.dto.PlanGroupRequestDto;
import com.splanet.splanet.gpt.service.GptPlanSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt/plan")
@RequiredArgsConstructor
public class GptPlanSaveController {

    private final GptPlanSaveService gptPlanSaveService;

    @PostMapping("/save")
    public ResponseEntity<Void> saveGptResponsePlans(@RequestBody PlanGroupRequestDto planGroupRequestDto) {
        gptPlanSaveService.saveGptResponsePlans(planGroupRequestDto);
        return ResponseEntity.ok().build();
    }
}
