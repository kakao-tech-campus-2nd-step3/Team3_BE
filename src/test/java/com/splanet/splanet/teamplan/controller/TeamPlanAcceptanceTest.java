package com.splanet.splanet.teamplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.team.entity.UserTeamRole;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.repository.TeamPlanRepository;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeamPlanAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamUserRelationRepository teamUserRelationRepository;

    @Autowired
    private TeamPlanRepository teamPlanRepository;

    private String userAccessToken;
    private Long userId;
    private Long friendId;
    private Long teamId;
    private Long planId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .nickname("testuser")
                .profileImage("testimage.png")
                .build();
        userRepository.save(user);
        userId = user.getId();

        User friend = User.builder()
                .nickname("frienduser")
                .profileImage("friendimage.png")
                .build();
        userRepository.save(friend);
        friendId = friend.getId();

        Team team = new Team("친구팀", user);
        teamRepository.save(team);
        teamId = team.getId();

        TeamUserRelation userRelation = new TeamUserRelation(team, user, UserTeamRole.ADMIN);
        TeamUserRelation friendRelation = new TeamUserRelation(team, friend, UserTeamRole.MEMBER);
        teamUserRelationRepository.save(userRelation);
        teamUserRelationRepository.save(friendRelation);

        userAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    void 팀플랜_생성후_수정후_삭제() throws Exception {
        TeamPlanRequestDto requestDto = new TeamPlanRequestDto(
                "친구 팀 플랜",
                "친구 팀에서 만든 플랜",
                LocalDateTime.parse("2024-11-12T10:00:00"),
                LocalDateTime.parse("2024-11-12T11:00:00"),
                true,
                true
        );

        String response = mockMvc.perform(post("/api/teams/{teamId}/plans", teamId)
                        .header("Authorization", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("친구 팀 플랜"))
                .andExpect(jsonPath("$.description").value("친구 팀에서 만든 플랜"))
                .andExpect(jsonPath("$.startDate").value("2024-11-12T10:00:00"))
                .andExpect(jsonPath("$.endDate").value("2024-11-12T11:00:00"))
                .andExpect(jsonPath("$.accessibility").value(true))
                .andExpect(jsonPath("$.isCompleted").value(true))
                .andReturn().getResponse().getContentAsString();

        planId = JsonPath.parse(response).read("$.id", Long.class);

        TeamPlanRequestDto updateRequestDto = new TeamPlanRequestDto(
                "수정된 친구 팀 플랜",
                "수정된 플랜 설명",
                LocalDateTime.parse("2024-11-12T11:00:00"),
                LocalDateTime.parse("2024-11-12T12:00:00"),
                false,
                false
        );

        mockMvc.perform(put("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 친구 팀 플랜"))
                .andExpect(jsonPath("$.description").value("수정된 플랜 설명"))
                .andExpect(jsonPath("$.startDate").value("2024-11-12T11:00:00"))
                .andExpect(jsonPath("$.endDate").value("2024-11-12T12:00:00"))
                .andExpect(jsonPath("$.accessibility").value(false))
                .andExpect(jsonPath("$.isCompleted").value(false));

        mockMvc.perform(delete("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", userAccessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/teams/{teamId}/plans/{planId}", teamId, planId)
                        .header("Authorization", userAccessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("플랜이 존재하지 않습니다."));
    }
}
