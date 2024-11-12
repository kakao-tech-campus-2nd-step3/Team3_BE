package com.splanet.splanet.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.service.PlanService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanService planService;

    private String accessToken;
    private Long userId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .nickname("testuser")
                .profileImage("testimage.png")
                .build();
        userRepository.save(user);
        userId = user.getId();

        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    @DisplayName("플랜 생성 성공 후 조회 - 생성된 플랜이 정확히 조회됨")
    void 플랜_생성_성공_후_정확히_조회() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        String planResponse = mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 플랜"))
                .andExpect(jsonPath("$.description").value("테스트 설명"))
                .andReturn().getResponse().getContentAsString();

        Long planId = objectMapper.readValue(planResponse, PlanResponseDto.class).getId();

        mockMvc.perform(get("/api/plans/" + planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 플랜"))
                .andExpect(jsonPath("$.description").value("테스트 설명"));
    }

    @Test
    @DisplayName("플랜 생성 실패 - 시작 날짜가 종료 날짜보다 이후")
    void 플랜_생성_실패_시작날짜가_종료날짜_이후() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("잘못된 날짜 플랜")
                .description("설명")
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("시작 시간은 종료 시간보다 이후일 수 없습니다.")));
    }

    @Test
    @DisplayName("플랜 생성 실패 - 필수 필드 누락")
    void 플랜_생성_실패_필수필드_누락() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .description("설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build(); // 제목 누락

        mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("제목은 공백일 수 없습니다.")));
    }

    @Test
    @DisplayName("모든 플랜 조회 성공 - 생성된 모든 플랜이 반환됨")
    void 모든_플랜_조회_성공() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("조회 테스트 플랜")
                .description("조회용 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        planService.createPlan(userId, requestDto);

        mockMvc.perform(get("/api/plans")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("조회 테스트 플랜"))
                .andExpect(jsonPath("$[0].description").value("조회용 설명"));
    }

    @Test
    @DisplayName("플랜 업데이트 성공 - 데이터가 올바르게 수정됨")
    void 플랜_업데이트_성공() throws Exception {
        PlanRequestDto createRequestDto = PlanRequestDto.builder()
                .title("초기 플랜")
                .description("초기 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        String createdPlanResponse = mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andReturn().getResponse().getContentAsString();

        Long planId = objectMapper.readValue(createdPlanResponse, PlanResponseDto.class).getId();

        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(put("/api/plans/" + planId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 플랜"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("플랜 업데이트 실패 - 존재하지 않는 플랜")
    void 플랜_업데이트_실패_존재하지않는플랜() throws Exception {
        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정 실패 플랜")
                .description("수정 실패 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(put("/api/plans/9999")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("플랜이 존재하지 않습니다.")));
    }

    @Test
    @DisplayName("플랜 삭제 성공 - 삭제 후 조회 실패")
    void 플랜_삭제_성공_후_조회실패() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("삭제 테스트 플랜")
                .description("삭제용 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        String createdPlanResponse = mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn().getResponse().getContentAsString();

        Long planId = objectMapper.readValue(createdPlanResponse, PlanResponseDto.class).getId();

        mockMvc.perform(delete("/api/plans/" + planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/plans/" + planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("플랜이 존재하지 않습니다.")));
    }
}
