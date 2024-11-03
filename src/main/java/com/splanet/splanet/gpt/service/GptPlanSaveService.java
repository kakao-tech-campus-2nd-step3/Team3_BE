package com.splanet.splanet.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.InvalidPlanFormatException;
import com.splanet.splanet.previewplan.dto.PlanGroupRequestDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GptPlanSaveService {

    private final PreviewPlanService previewPlanService;
    private final ObjectMapper objectMapper;

    public void saveGptResponsePlans(String responseContent) {
        PlanGroupRequestDto planGroupRequestDto = parsePlanGroupRequestDto(responseContent);

        String deviceId = planGroupRequestDto.deviceId();
        String groupId = planGroupRequestDto.groupId();

        previewPlanService.deleteAllPreviewPlansByDeviceId(deviceId);

        for (var planCardRequestDto : planGroupRequestDto.planCards()) {
            previewPlanService.savePlanCard(deviceId, groupId, planCardRequestDto);
        }
    }

    private PlanGroupRequestDto parsePlanGroupRequestDto(String responseContent) {
        try {
            return objectMapper.readValue(responseContent, PlanGroupRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new InvalidPlanFormatException(responseContent); // JSON 형식이 아닐 경우 예외 발생
        }
    }
}
