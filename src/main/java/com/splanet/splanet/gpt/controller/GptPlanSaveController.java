package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.gpt.service.GptPlanSaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt/plan")
@Tag(name = "GPT-Plan save", description = "GPT로 생성된 플랜을 저장한다.")
@RequiredArgsConstructor
public class GptPlanSaveController {

    private final GptPlanSaveService gptPlanSaveService;

    @PostMapping("/save")
    public ResponseEntity<Void> saveGptResponsePlans(@RequestBody String planGroupResponse) {
        gptPlanSaveService.saveGptResponsePlans(planGroupResponse); // JSON 파싱 및 저장 수행
        return ResponseEntity.ok().build();
    }
}
