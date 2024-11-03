package com.splanet.splanet.gpt.service;

import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanGroupRequestDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GptPlanSaveService {

    private final PreviewPlanService previewPlanService;

    public void saveGptResponsePlans(PlanGroupRequestDto planGroupRequestDto) {
        String deviceId = planGroupRequestDto.deviceId();
        String groupId = planGroupRequestDto.groupId();
        
        previewPlanService.deleteAllPreviewPlansByDeviceId(deviceId);

        // JSON 응답에서 planCards 리스트를 순회하며 개별 PlanCard를 저장
        for (PlanCardRequestDto planCardRequestDto : planGroupRequestDto.planCards()) {
            previewPlanService.savePlanCard(deviceId, groupId, planCardRequestDto);
        }
    }
}
