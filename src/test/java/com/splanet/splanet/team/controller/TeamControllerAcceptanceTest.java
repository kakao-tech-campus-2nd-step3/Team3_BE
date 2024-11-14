package com.splanet.splanet.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.team.entity.*;
import com.splanet.splanet.team.repository.TeamInvitationRepository;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.team.repository.TeamUserRelationRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeamControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private String adminAccessToken;
    private String invitedUserAccessToken;
    private Long teamId;
    private Long adminUserId;
    private Long invitedUserId;

    @BeforeEach
    void setUp() {
        User adminUser = User.builder()
                .nickname("adminUser")
                .profileImage("admin.png")
                .build();
        userRepository.save(adminUser);
        adminUserId = adminUser.getId();

        User invitedUser = User.builder()
                .nickname("invitedUser")
                .profileImage("invited.png")
                .build();
        userRepository.save(invitedUser);
        invitedUserId = invitedUser.getId();

        Team team = new Team("Test Team", adminUser);
        teamRepository.save(team);
        teamId = team.getId();

        TeamUserRelation adminRelation = new TeamUserRelation(team, adminUser, UserTeamRole.ADMIN);
        teamUserRelationRepository.save(adminRelation);

        adminAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(adminUserId);
        invitedUserAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(invitedUserId);
    }

    @Test
    void 팀생성후_유저초대_유저가_수락후_내보내기() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .header("Authorization", adminAccessToken)
                        .param("teamName", "새로운 팀"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("새로운 팀"))
                .andExpect(jsonPath("$.user.nickname").value("adminUser"));

        mockMvc.perform(post("/api/teams/{teamId}/invite", teamId)
                        .header("Authorization", adminAccessToken)
                        .param("nickname", "invitedUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("invitedUser"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.status").value("PENDING"));

        TeamInvitation invitation = teamInvitationRepository.findAllByUserAndStatus(userRepository.findById(invitedUserId).get(), InvitationStatus.PENDING).get(0);
        assertThat(invitation.getUser().getId()).isEqualTo(invitedUserId);
        assertThat(invitation.getTeam().getId()).isEqualTo(teamId);
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);

        mockMvc.perform(put("/api/teams/invitation/{invitationId}/response", invitation.getId())
                        .header("Authorization", invitedUserAccessToken)
                        .param("isAccepted", "true"))
                .andExpect(status().isNoContent());

        TeamInvitation updatedInvitation = teamInvitationRepository.findById(invitation.getId()).orElseThrow();
        assertThat(updatedInvitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);

        TeamUserRelation userRelation = teamUserRelationRepository.findByTeamAndUser(teamRepository.findById(teamId).get(), userRepository.findById(invitedUserId).get()).orElseThrow();
        assertThat(userRelation.getUser().getId()).isEqualTo(invitedUserId);

        mockMvc.perform(delete("/api/teams/{teamId}/users/{userId}", teamId, invitedUserId)
                        .header("Authorization", adminAccessToken))
                .andExpect(status().isNoContent());

        TeamUserRelation removedUserRelation = teamUserRelationRepository.findByTeamAndUser(teamRepository.findById(teamId).get(), userRepository.findById(invitedUserId).get()).orElse(null);
        assertThat(removedUserRelation).isNull();
    }
}