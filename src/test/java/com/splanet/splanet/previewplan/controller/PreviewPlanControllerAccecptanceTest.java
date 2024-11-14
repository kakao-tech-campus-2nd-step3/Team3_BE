package com.splanet.splanet.previewplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.entity.PlanCard;
import com.splanet.splanet.previewplan.repository.PlanCardRepository;
import com.splanet.splanet.previewplan.repository.PlanGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PreviewPlanControllerAccecptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PlanCardRepository planCardRepository;

    @Autowired
    private PlanGroupRepository planGroupRepository;

    private static final String BASE_URL = "/api/preview-plan";

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    private void saveSamplePlanCard(String deviceId, String groupId, String cardId) {
        PlanCard planCard = PlanCard.builder()
                .customKey(deviceId + ":" + groupId + ":" + cardId)
                .deviceId(deviceId)
                .groupId(groupId)
                .cardId(cardId)
                .title("Sample Title")
                .description("Sample Description")
                .startDate("2024-01-01")
                .endDate("2024-12-31")
                .expiration(3600L)
                .build();
        planCardRepository.save(planCard);
    }

    @Test
    void 플랜카드_생성후_조회수_수정후_삭제() throws Exception {
        String deviceId = "testDevice";
        String groupId = "testGroup";
        String cardId = "testCard";

        PlanCardRequestDto requestDto = new PlanCardRequestDto("Title", "Description", "2024-01-01", "2024-12-31");
        mockMvc.perform(post(BASE_URL + "/card")
                        .param("deviceId", deviceId)
                        .param("groupId", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Description"));

        saveSamplePlanCard(deviceId, groupId, cardId);

        mockMvc.perform(get(BASE_URL + "/card/{deviceId}/{groupId}/{cardId}", deviceId, groupId, cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Title"));

        PlanCardRequestDto updateRequest = new PlanCardRequestDto("Updated Title", "Updated Description", "2024-01-01", "2024-12-31");
        mockMvc.perform(put(BASE_URL + "/card/{deviceId}/{groupId}/{cardId}", deviceId, groupId, cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        mockMvc.perform(delete(BASE_URL + "/card/{deviceId}/{groupId}/{cardId}", deviceId, groupId, cardId))
                .andExpect(status().isOk());

        assertFalse(planCardRepository.findById(deviceId + ":" + groupId + ":" + cardId).isPresent());
    }
}