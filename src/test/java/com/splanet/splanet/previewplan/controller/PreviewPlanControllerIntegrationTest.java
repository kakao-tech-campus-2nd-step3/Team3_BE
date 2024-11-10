package com.splanet.splanet.previewplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.service.PreviewPlanService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PreviewPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreviewPlanService previewPlanService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        User user = User.builder()
                .nickname("testuser")
                .profileImage("testimage.png")
                .build();
        userRepository.save(user);
        userId = user.getId();

        // JWT 토큰 생성
        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    @WithMockUser
    void 플랜카드_생성_성공() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        PlanCardRequestDto planCardRequestDto = new PlanCardRequestDto(
                "테스트 플랜 카드",
                "플랜 카드 설명",
                now.toString(),
                now.plusHours(2).toString()
        );

        long startTimestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endTimestamp = now.plusHours(2).atZone(ZoneId.systemDefault()).toEpochSecond();

        PlanCardResponseDto planCardResponseDto = new PlanCardResponseDto(
                "device-123",
                "group-456",
                "ed0525be",
                "테스트 플랜 카드",
                "플랜 카드 설명",
                startTimestamp,
                endTimestamp
        );

        mockMvc.perform(post("/api/preview-plan/card")
                        .param("deviceId", "device-123")
                        .param("groupId", "group-456")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planCardRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(planCardResponseDto.deviceId()))
                .andExpect(jsonPath("$.groupId").value(planCardResponseDto.groupId()))
                .andExpect(jsonPath("$.title").value(planCardResponseDto.title()))
                .andExpect(jsonPath("$.description").value(planCardResponseDto.description()))
                .andExpect(jsonPath("$.startTimestamp").value(startTimestamp))
                .andExpect(jsonPath("$.endTimestamp").value(endTimestamp));
    }

    @Test
    @WithMockUser
    void 플랜카드_조회_성공() throws Exception {
        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();
        PlanCardRequestDto planCardRequestDto = new PlanCardRequestDto(
                "테스트 플랜 카드",
                "플랜 카드 설명",
                now.toString(),
                now.plusHours(2).toString()
        );

        long startTimestamp = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endTimestamp = now.plusHours(2).atZone(ZoneId.systemDefault()).toEpochSecond();

        MvcResult result = mockMvc.perform(post("/api/preview-plan/card")
                        .param("deviceId", "device-123")
                        .param("groupId", "group-456")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planCardRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String cardId = JsonPath.read(result.getResponse().getContentAsString(), "$.cardId");

        PlanCardResponseDto planCardResponseDto = new PlanCardResponseDto(
                "device-123",
                "group-456",
                cardId,  // 생성된 cardId를 사용
                "테스트 플랜 카드",
                "플랜 카드 설명",
                startTimestamp,
                endTimestamp
        );

        mockMvc.perform(get("/api/preview-plan/card/{deviceId}/{groupId}/{cardId}", "device-123", "group-456", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(planCardResponseDto.deviceId()))
                .andExpect(jsonPath("$.groupId").value(planCardResponseDto.groupId()))
                .andExpect(jsonPath("$.title").value(planCardResponseDto.title()))
                .andExpect(jsonPath("$.description").value(planCardResponseDto.description()))
                .andExpect(jsonPath("$.startTimestamp").value(startTimestamp))
                .andExpect(jsonPath("$.endTimestamp").value(endTimestamp));
    }
}
