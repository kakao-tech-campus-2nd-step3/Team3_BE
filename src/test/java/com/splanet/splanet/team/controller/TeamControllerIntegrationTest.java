package com.splanet.splanet.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.entity.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private Long adminUserId;
    private Long invitedUserId;

    private Team team;

    @BeforeEach
    void setUp() {
        // 관리자 유저 생성
        User adminUser = User.builder()
                .nickname("adminUser")
                .profileImage("admin.png")
                .build();
        userRepository.save(adminUser);
        adminUserId = adminUser.getId();

        // 초대할 유저 생성
        User invitedUser = User.builder()
                .nickname("invitedUser")
                .profileImage("invited.png")
                .build();
        userRepository.save(invitedUser);
        invitedUserId = invitedUser.getId();

        // 팀 생성
        team = new Team("Test Team", adminUser);
        teamRepository.save(team);

        // 관리자 권한 설정
        TeamUserRelation adminRelation = new TeamUserRelation(team, adminUser, UserTeamRole.ADMIN);
        teamUserRelationRepository.save(adminRelation);

        // JWT 토큰 생성
        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(adminUserId);
    }

    @Test
    void 팀_유저_초대_성공() throws Exception {
        mockMvc.perform(post("/api/teams/{teamId}/invite", team.getId())
                        .header("Authorization", accessToken)
                        .param("nickname", "invitedUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("invitedUser"))
                .andExpect(jsonPath("$.teamId").value(team.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        TeamInvitation invitation = teamInvitationRepository.findAllByUserAndStatus(userRepository.findById(invitedUserId).get(), InvitationStatus.PENDING).get(0);
        assertThat(invitation.getUser().getId()).isEqualTo(invitedUserId);
        assertThat(invitation.getTeam().getId()).isEqualTo(team.getId());
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    void 팀_유저_초대_실패_관리자권한없음() throws Exception {
        // 다른 유저로 JWT 토큰 발급 (관리자 권한 없음)
        User nonAdminUser = User.builder()
                .nickname("nonAdminUser")
                .profileImage("nonAdmin.png")
                .build();
        userRepository.save(nonAdminUser);

        String nonAdminToken = "Bearer " + jwtTokenProvider.createAccessToken(nonAdminUser.getId());

        mockMvc.perform(post("/api/teams/{teamId}/invite", team.getId())
                        .header("Authorization", nonAdminToken)
                        .param("nickname", "invitedUser"))
                .andExpect(status().isForbidden());
    }

    @Test
    void 팀_생성_성공() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .header("Authorization", accessToken)
                        .param("teamName", "New Team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("New Team"))
                .andExpect(jsonPath("$.user.nickname").value("adminUser"));
    }

    @Test
    void 팀_멤버_조회_성공() throws Exception {
        mockMvc.perform(get("/api/teams/{teamId}/members", team.getId())
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("adminUser"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void 초대_취소_성공() throws Exception {
        TeamInvitationDto invitationDto = teamService.inviteUserToTeamByNickname(team.getId(), adminUserId, "invitedUser");
        Long invitationId = invitationDto.getInvitationId();

        mockMvc.perform(delete("/api/teams/invitation/{invitationId}/cancel", invitationId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void 유저_권한_수정_성공() throws Exception {
        TeamInvitationDto invitationDto = teamService.inviteUserToTeamByNickname(team.getId(), adminUserId, "invitedUser");
        Long invitationId = invitationDto.getInvitationId();

        String invitedUserToken = "Bearer " + jwtTokenProvider.createAccessToken(invitedUserId);

        mockMvc.perform(put("/api/teams/invitation/{invitationId}/response", invitationId)
                        .header("Authorization", invitedUserToken)
                        .param("isAccepted", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/teams/{teamId}/users/{userId}/role", team.getId(), invitedUserId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(invitedUserId))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        TeamUserRelation updatedRelation = teamUserRelationRepository.findByTeamAndUser(team, userRepository.findById(invitedUserId).get()).get();
        assertThat(updatedRelation.getRole()).isEqualTo(UserTeamRole.ADMIN);
    }

    @Test
    void 초대_수락_성공() throws Exception {
        TeamInvitationDto invitationDto = teamService.inviteUserToTeamByNickname(team.getId(), adminUserId, "invitedUser");
        Long invitationId = invitationDto.getInvitationId();

        String invitedUserToken = "Bearer " + jwtTokenProvider.createAccessToken(invitedUserId);

        mockMvc.perform(put("/api/teams/invitation/{invitationId}/response", invitationId)
                        .header("Authorization", invitedUserToken)
                        .param("isAccepted", "true"))
                .andExpect(status().isNoContent());

        TeamInvitation updatedInvitation = teamInvitationRepository.findById(invitationId).orElseThrow();
        assertThat(updatedInvitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);

        TeamUserRelation userRelation = teamUserRelationRepository.findByTeamAndUser(team, userRepository.findById(invitedUserId).get()).orElseThrow();
        assertThat(userRelation.getUser().getId()).isEqualTo(invitedUserId);
    }

    @Test
    void 유저_내보내기_실패_권한없음() throws Exception {
        User nonAdminUser = User.builder()
                .nickname("nonAdminUser")
                .profileImage("nonAdmin.png")
                .build();
        userRepository.save(nonAdminUser);
        String nonAdminToken = "Bearer " + jwtTokenProvider.createAccessToken(nonAdminUser.getId());

        mockMvc.perform(delete("/api/teams/{teamId}/users/{userId}", team.getId(), invitedUserId)
                        .header("Authorization", nonAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void 유저_내보내기_성공() throws Exception {
        // 초대된 유저를 팀에 추가
        TeamInvitationDto invitationDto = teamService.inviteUserToTeamByNickname(team.getId(), adminUserId, "invitedUser");
        teamService.handleInvitationResponse(invitationDto.getInvitationId(), invitedUserId, true);

        // 내보내기 요청
        mockMvc.perform(delete("/api/teams/{teamId}/users/{userId}", team.getId(), invitedUserId)
                        .header("Authorization", accessToken))
                .andExpect(status().isNoContent());
        // 팀에서 유저가 제거되었는지 확인
        Optional<TeamUserRelation> userRelation = teamUserRelationRepository.findByTeamAndUser(team, userRepository.findById(invitedUserId).get());
        assertThat(userRelation).isEmpty();
    }
}