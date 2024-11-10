package com.splanet.splanet.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.team.service.TeamService;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamInvitationRepository teamInvitationRepository;

    @Autowired
    private TeamUserRelationRepository teamUserRelationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String accessToken;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 사용자 생성
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
    void 팀_생성_성공() throws Exception {
        String teamName = "My New Team";

        mockMvc.perform(post("/api/teams")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("teamName", teamName)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(teamName))
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.teamMembers").isEmpty());

        Team team = teamRepository.findByTeamName(teamName).orElseThrow();
        assert team.getTeamName().equals(teamName);
        assert team.getUser().getId().equals(userId);

        TeamUserRelation teamUserRelation = teamUserRelationRepository.findByTeamAndUser(team, team.getUser()).orElseThrow();
        assert teamUserRelation.getRole() == UserTeamRole.ADMIN;
    }

//    @Test
//    void 팀_생성_팀이름_누락() throws Exception {
//        mockMvc.perform(post("/api/teams")
//                        .header("Authorization", accessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("userId", userId.toString()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errorCode").value("TEAM_NAME_NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value("팀이름이 비어 있습니다."));
//    }
//
//    @Test
//    void 팀_생성_존재하지_않는_유저() throws Exception {
//        Long invalidUserId = 999L;
//        String teamName = "My Invalid Team";
//
//        mockMvc.perform(post("/api/teams")
//                        .header("Authorization", accessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("teamName", teamName)
//                        .param("userId", invalidUserId.toString()))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("유저가 존재하지 않습니다."));
//    }
}
