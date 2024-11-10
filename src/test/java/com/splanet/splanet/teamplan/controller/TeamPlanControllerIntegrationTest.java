package com.splanet.splanet.teamplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.teamplan.repository.TeamPlanRepository;
import com.splanet.splanet.teamplan.service.TeamPlanService;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeamPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamPlanService teamPlanService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TeamUserRelationRepository teamUserRelationRepository;

    @Autowired
    private TeamPlanRepository teamPlanRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;
    private Long userId;
    private Long teamId;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        User user = User.builder()
                .nickname("testuser")
                .profileImage("testimage.png")
                .build();
        userRepository.save(user);
        userId = user.getId();

        // 팀 생성
        Team team = new Team("팀네임여기있음!", user);
        teamRepository.save(team);
        teamId = team.getId();

        // 팀 사용자 관계 설정
        TeamUserRelation relation = new TeamUserRelation(team, user, UserTeamRole.ADMIN);
        teamUserRelationRepository.save(relation);

        // JWT 토큰 생성
        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    void 팀플랜_생성_성공() throws Exception {
        // 팀 플랜 생성 요청 DTO
        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "11/10 테스트용 플랜",
                "11/10 테스트용 플랜",
                LocalDateTime.parse("2024-11-10T09:59:23.542"),
                LocalDateTime.parse("2024-11-10T09:59:23.542"),
                true,
                true
        );

        // 팀 플랜을 생성하는 요청
        mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("11/10 테스트용 플랜"))
                .andExpect(jsonPath("$.description").value("11/10 테스트용 플랜"))
                .andExpect(jsonPath("$.startDate").value("2024-11-10T09:59:23.542"))
                .andExpect(jsonPath("$.endDate").value("2024-11-10T09:59:23.542"))
                .andExpect(jsonPath("$.accessibility").value(true))
                .andExpect(jsonPath("$.isCompleted").value(true));
    }

    @Test
    void 팀플랜_생성_실패_필드누락() throws Exception {
        TeamPlanRequestDto invalidRequestDto = new TeamPlanRequestDto(
                null,
                "필드 누락 테스트 설명",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                true,
                false
        );

        mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("공백일 수 없습니다"));
    }

    @Test
    void 팀플랜_생성_실패_권한없음() throws Exception {
        User otherUser = User.builder()
                .nickname("unauthorizedUser")
                .profileImage("otherimage.png")
                .build();
        userRepository.save(otherUser);

        String otherUserAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(otherUser.getId());

        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "권한 없는 사용자 테스트 플랜",
                "권한 없는 사용자 설명",
                LocalDateTime.parse("2024-11-12T09:00:00"),
                LocalDateTime.parse("2024-11-12T10:00:00"),
                true,
                false
        );

        mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", otherUserAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    void 팀플랜_조회_성공() throws Exception {
        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "11/10 테스트용 플랜",
                "11/10 테스트용 플랜",
                LocalDateTime.parse("2024-11-10T09:59:23.542"),
                LocalDateTime.parse("2024-11-10T09:59:23.542"),
                true,
                true
        );

        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("11/10 테스트용 플랜"))
                .andReturn().getResponse().getContentAsString();

        String planId = JsonPath.parse(response).read("$.id").toString();

        mockMvc.perform(get("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("11/10 테스트용 플랜"));
    }

    @Test
    void 팀플랜_수정_성공() throws Exception {
        TeamPlanRequestDto createRequestDto = new TeamPlanRequestDto(
                "수정 전 테스트 플랜",
                "수정 전 테스트 설명",
                LocalDateTime.parse("2024-11-11T09:00:00"),
                LocalDateTime.parse("2024-11-11T10:00:00"),
                true,
                false
        );

        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long planId = JsonPath.parse(response).read("$.id", Long.class);

        TeamPlanRequestDto updateRequestDto = new TeamPlanRequestDto(
                "수정된 플랜 제목",
                "수정된 플랜 설명",
                LocalDateTime.parse("2024-11-11T10:00:00"),
                LocalDateTime.parse("2024-11-11T11:00:00"),
                false,
                true
        );

        mockMvc.perform(put("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 플랜 제목"))
                .andExpect(jsonPath("$.description").value("수정된 플랜 설명"))
                .andExpect(jsonPath("$.startDate").value("2024-11-11T10:00:00"))
                .andExpect(jsonPath("$.endDate").value("2024-11-11T11:00:00"))
                .andExpect(jsonPath("$.accessibility").value(false))
                .andExpect(jsonPath("$.isCompleted").value(true));
    }

    @Test
    void 팀플랜_수정_플랜없음() throws Exception {
        // 존재하지 않는 플랜 ID로 수정 요청을 보낼 때
        mockMvc.perform(put("/api/teams/{teamId}/plans/{planId}", teamId, 999L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TeamPlanRequestDto(
                                "수정된 플랜 제목", "수정된 플랜 설명", LocalDateTime.now(), LocalDateTime.now(), true, true))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("플랜이 존재하지 않습니다."));
    }

    @Test
    void 팀플랜_수정_권한없음() throws Exception {
        User otherUser = User.builder()
                .nickname("unauthorizedUser")
                .profileImage("otherimage.png")
                .build();
        userRepository.save(otherUser);

        String otherUserAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(otherUser.getId());

        TeamPlanRequestDto createRequestDto = new TeamPlanRequestDto(
                "권한 테스트 플랜",
                "권한 테스트 설명",
                LocalDateTime.parse("2024-11-12T09:00:00"),
                LocalDateTime.parse("2024-11-12T10:00:00"),
                true,
                false
        );
        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long planId = JsonPath.parse(response).read("$.id", Long.class);

        TeamPlanRequestDto updateRequestDto = new TeamPlanRequestDto(
                "권한 없는 수정",
                "권한 없는 설명",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                true,
                true
        );

        mockMvc.perform(put("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", otherUserAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    void 팀플랜_삭제_성공() throws Exception {
        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "삭제용 테스트 플랜",
                "삭제용 테스트 설명",
                LocalDateTime.parse("2024-11-11T09:59:23.542"),
                LocalDateTime.parse("2024-11-11T10:59:23.542"),
                true,
                false
        );

        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long planId = JsonPath.parse(response).read("$.id", Long.class);

        mockMvc.perform(delete("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("플랜이 존재하지 않습니다."));
    }

    @Test
    void 팀플랜_삭제_실패_플랜없음() throws Exception {
        Long nonExistentPlanId = 999L;

        mockMvc.perform(delete("/api/teams/{teamId}/plans/{planId}", teamId, nonExistentPlanId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("플랜이 존재하지 않습니다."));
    }

    @Test
    void 팀플랜_삭제_실패_권한없음() throws Exception {
        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "삭제 권한 없는 사용자 테스트 플랜",
                "삭제 권한 없는 사용자 설명",
                LocalDateTime.parse("2024-11-11T09:00:00"),
                LocalDateTime.parse("2024-11-11T10:00:00"),
                true,
                false
        );

        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long planId = JsonPath.parse(response).read("$.id", Long.class);

        User otherUser = User.builder()
                .nickname("unauthorizedUser")
                .profileImage("otherimage.png")
                .build();
        userRepository.save(otherUser);

        String otherUserAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(otherUser.getId());

        mockMvc.perform(delete("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", otherUserAccessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }
}