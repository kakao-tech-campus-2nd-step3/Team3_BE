package com.splanet.splanet.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.service.PlanService;
import com.splanet.splanet.jwt.JwtTokenProvider;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanService planService;

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

    // 플랜 생성 관련 테스트
    @Test
    @DisplayName("플랜 생성 성공")
    @WithMockUser
    void 플랜_생성_성공() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 플랜"));
    }

    @Test
    @DisplayName("플랜 생성 실패 - 유효하지 않은 날짜 값")
    @WithMockUser
    void 플랜_생성_실패_유효하지않은_날짜() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
                .startDate(null)
                .endDate(null)
                .build();

        mockMvc.perform(post("/api/plans")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("시작 또는 종료 시간은 null일 수 없습니다.")));
    }

    @Test
    @DisplayName("플랜 생성 실패 - 시작 날짜가 종료 날짜보다 이후")
    @WithMockUser
    void 플랜_생성_실패_시작날짜가_종료날짜_이후() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
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

    // 플랜 조회 관련 테스트
    @Test
    @DisplayName("플랜 조회 성공")
    @WithMockUser
    void 플랜_조회_성공() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("조회 테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        PlanResponseDto createdPlan = planService.createPlan(userId, requestDto);

        mockMvc.perform(get("/api/plans/" + createdPlan.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("조회 테스트 플랜"));
    }

    @Test
    @DisplayName("플랜 조회 실패 - 존재하지 않는 플랜")
    @WithMockUser
    void 플랜_조회_실패_존재하지않는플랜() throws Exception {
        mockMvc.perform(get("/api/plans/9999")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("플랜이 존재하지 않습니다.")));
    }

    // 플랜 업데이트 관련 테스트
    @Test
    @DisplayName("플랜 업데이트 성공")
    @WithMockUser
    void 플랜_업데이트_성공() throws Exception {
        PlanRequestDto createRequestDto = PlanRequestDto.builder()
                .title("초기 플랜")
                .description("초기 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        PlanResponseDto createdPlan = planService.createPlan(userId, createRequestDto);

        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(put("/api/plans/" + createdPlan.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 플랜"));
    }

    @Test
    @DisplayName("플랜 업데이트 실패 - 존재하지 않는 플랜")
    @WithMockUser
    void 플랜_업데이트_실패_존재하지않는플랜() throws Exception {
        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
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
    @DisplayName("플랜 업데이트 실패 - 유효하지 않은 날짜 값")
    @WithMockUser
    void 플랜_업데이트_실패_유효하지않은_날짜() throws Exception {
        // given
        PlanRequestDto createRequestDto = PlanRequestDto.builder()
                .title("초기 플랜")
                .description("초기 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        PlanResponseDto createdPlan = planService.createPlan(userId, createRequestDto);

        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .startDate(null)
                .endDate(null)
                .build();
        // when & then
        mockMvc.perform(put("/api/plans/" + createdPlan.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("시작 또는 종료 시간은 null일 수 없습니다.")));
    }

    @Test
    @DisplayName("플랜 업데이트 실패 - 시작 날짜가 종료 날짜보다 이후")
    @WithMockUser
    void 플랜_업데이트_실패_시작날짜가_종료날짜_이후() throws Exception {
        // given
        PlanRequestDto createRequestDto = PlanRequestDto.builder()
                .title("초기 플랜")
                .description("초기 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        PlanResponseDto createdPlan = planService.createPlan(userId, createRequestDto);

        PlanRequestDto updateRequestDto = PlanRequestDto.builder()
                .title("수정된 플랜")
                .description("수정된 설명")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        mockMvc.perform(put("/api/plans/" + createdPlan.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("시작 시간은 종료 시간보다 이후일 수 없습니다.")));
    }

    // 플랜 삭제 관련 테스트
    @Test
    @DisplayName("플랜 삭제 성공")
    @WithMockUser
    void 플랜_삭제_성공() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("삭제 테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        PlanResponseDto createdPlan = planService.createPlan(userId, requestDto);

        mockMvc.perform(delete("/api/plans/" + createdPlan.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("플랜 삭제 실패 - 존재하지 않는 플랜")
    @WithMockUser
    void 플랜_삭제_실패_존재하지않는플랜() throws Exception {
        mockMvc.perform(delete("/api/plans/9999")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("플랜이 존재하지 않습니다.")));
    }
}
