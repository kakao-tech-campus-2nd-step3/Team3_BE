package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.gpt.service.GptPlanSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GptPlanSaveController implements GptPlanSaveApi{

    private final GptPlanSaveService gptPlanSaveService;

    @Override
    public ResponseEntity<Void> saveGptResponsePlans(@RequestBody String planGroupResponse) {
        gptPlanSaveService.saveGptResponsePlans(planGroupResponse); // JSON 파싱 및 저장 수행
        return ResponseEntity.ok().build();
    }
}