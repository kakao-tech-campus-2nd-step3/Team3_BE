package com.splanet.splanet.friend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friend.service.FriendService;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.service.PlanService;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FriendControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FriendService friendService;

    @Autowired
    private PlanService planService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

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

        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);
    }

    @Test
    @DisplayName("친구 목록 조회 성공")
    @WithMockUser
    void 친구_목록_조회_성공() throws Exception {
        // 친구 목록 조회 결과가 비어 있지 않음을 확인
        List<FriendResponse> friendResponses = friendService.getFriends(userId);

        // 친구 목록이 비어 있지 않으면 테스트 진행
        if (!friendResponses.isEmpty()) {
            mockMvc.perform(get("/api/friends")
                            .header("Authorization", accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nickname").value(friendResponses.get(0).nickname()))
                    .andExpect(jsonPath("$[0].profileImage").value(friendResponses.get(0).profileImage()));
        } else {
            mockMvc.perform(get("/api/friends")
                            .header("Authorization", accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Test
    @DisplayName("친구 목록 조회 실패 - 인증되지 않은 사용자")
    void 친구_목록_조회_실패_인증되지않은_사용자() throws Exception {
        mockMvc.perform(get("/api/friends")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("친구 플랜 조회 실패 - 인증되지 않은 사용자")
    void 친구_플랜_조회_실패_인증되지않은_사용자() throws Exception {
        Long friendId = 1L;

        mockMvc.perform(get("/api/friends/{friendId}/plans", friendId)
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("친구 삭제 성공")
    @WithMockUser
    void 친구_삭제_성공() throws Exception {
        // 친구 관계를 만들기 위한 유저 생성
        User friendUser = User.builder()
                .nickname("frienduser")
                .profileImage("friendimage.png")
                .build();
        userRepository.save(friendUser);

        // 기존 userId로 조회하여 친구 관계를 만들기 위해 user 객체를 조회
        User user = userRepository.findById(userId).orElseThrow();

        // 친구 관계 등록 (예: userId와 friendUserId 사이에 친구 관계가 만들어진다고 가정)
        friendRepository.save(new Friend(user, friendUser));

        mockMvc.perform(delete("/api/friends/{friendId}", friendUser.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 맺기 취소되었습니다!"));
    }

    @Test
    @DisplayName("친구 삭제 실패 - 친구가 아님")
    @WithMockUser
    void 친구_삭제_실패_친구가_아님() throws Exception {
        // 존재하지 않는 친구의 ID
        Long nonFriendId = 999L;

        mockMvc.perform(delete("/api/friends/{friendId}", nonFriendId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("친구가 아닙니다."));
    }

    @Test
    @DisplayName("친구 삭제 실패 - 인증되지 않은 사용자")
    void 친구_삭제_실패_인증되지않은_사용자() throws Exception {
        // 인증되지 않은 토큰
        mockMvc.perform(delete("/api/friends/{friendId}", 1L)
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @WithMockUser
    void 친구_플랜_조회_실패_친구가_아님() throws Exception {
        Long nonFriendId = 999L;

        mockMvc.perform(get("/api/friends/{friendId}/plans", nonFriendId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("친구가 아닙니다."));
    }
}