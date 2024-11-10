package com.splanet.splanet.teamplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

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

        Team team = new Team("팀네임여기있음!", user);
        System.out.println("팀네임출력해: " + team.getTeamName());
        teamRepository.save(team);

        TeamUserRelation relation = new TeamUserRelation(team, user, UserTeamRole.ADMIN);
        teamUserRelationRepository.save(relation);

        // JWT 토큰 생성
        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
        mockMvc.perform(post("/api/teams/{teamId}/plans", 1)
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


}