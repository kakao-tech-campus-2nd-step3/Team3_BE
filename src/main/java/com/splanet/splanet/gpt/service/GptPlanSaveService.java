package com.splanet.splanet.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.splanet.splanet.core.exception.InvalidPlanFormatException;
import com.splanet.splanet.previewplan.dto.PlanGroupRequestDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GptPlanSaveService {

    private final PreviewPlanService previewPlanService;
    private final ObjectMapper objectMapper;

    public void saveGptResponsePlans(String responseContent) {
        String convertedContent = convertTimestampToISO(responseContent);
        PlanGroupRequestDto planGroupRequestDto = parsePlanGroupRequestDto(convertedContent);

        handlePlanGroup(planGroupRequestDto);
    }

    private String convertTimestampToISO(String responseContent) {
        try {
            JsonNode root = objectMapper.readTree(responseContent);
            JsonNode planCards = root.get("planCards");

            if (planCards.isArray()) {
                convertPlanCardsTimestampToISO(planCards);
            }

            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new InvalidPlanFormatException(responseContent);
        }
    }

    private void convertPlanCardsTimestampToISO(JsonNode planCards) {
        for (JsonNode planCard : planCards) {
            if (planCard instanceof ObjectNode) {
                convertTimestampField((ObjectNode) planCard, "startTimestamp", "startDate");
                convertTimestampField((ObjectNode) planCard, "endTimestamp", "endDate");
            }
        }
    }

    private void convertTimestampField(ObjectNode planCard, String timestampField, String dateField) {
        if (planCard.has(timestampField)) {
            long timestamp = planCard.get(timestampField).asLong();
            String isoDate = formatTimestampToISO(timestamp);
            planCard.put(dateField, isoDate);
        }
    }

  private String formatTimestampToISO(long timestamp) {
    return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.of("Asia/Seoul")) // 한국 시간으로 변환
            .toLocalDateTime()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

    private PlanGroupRequestDto parsePlanGroupRequestDto(String convertedContent) {
        try {
            return objectMapper.readValue(convertedContent, PlanGroupRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new InvalidPlanFormatException(convertedContent);
        }
    }

    private void handlePlanGroup(PlanGroupRequestDto planGroupRequestDto) {
        String deviceId = planGroupRequestDto.deviceId();
        String groupId = planGroupRequestDto.groupId();

        previewPlanService.deleteAllPreviewPlansByDeviceId(deviceId);

        for (var planCardRequestDto : planGroupRequestDto.planCards()) {
            previewPlanService.savePlanCard(deviceId, groupId, planCardRequestDto);
        }
    }
}
