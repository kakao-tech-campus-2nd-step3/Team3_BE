package com.splanet.splanet.friendRequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.FriendRequestCreateRequest;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FriendRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

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
    @WithMockUser
    void 친구_요청_실패_본인에게_친구_요청() throws Exception {
        FriendRequestCreateRequest request = new FriendRequestCreateRequest(userId);

        mockMvc.perform(post("/api/friends/requests")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("본인에게 친구요청을 보낼 수 없습니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_실패_이미_요청한_사용자() throws Exception {
        User receiverUser = User.builder()
                .nickname("friendUser")
                .profileImage("friendImage.png")
                .build();
        userRepository.save(receiverUser);

        // 이미 보낸 요청이 있다고 가정
        FriendRequestCreateRequest request = new FriendRequestCreateRequest(receiverUser.getId());
        friendRequestRepository.save(FriendRequest.builder()
                .requester(userRepository.findById(userId).orElseThrow())
                .receiver(receiverUser)
                .status(FriendRequest.Status.PENDING)
                .build());

        mockMvc.perform(post("/api/friends/requests")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 요청을 보냈습니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_실패_존재하지_않는_사용자() throws Exception {
        // 존재하지 않는 사용자 ID로 친구 요청 시도
        FriendRequestCreateRequest request = new FriendRequestCreateRequest(999L);

        mockMvc.perform(post("/api/friends/requests")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("유저가 존재하지 않습니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_수락_실패_이미_수락된_요청() throws Exception {
        // 이미 수락된 친구 요청을 시도
        User receiverUser = User.builder()
                .nickname("friendUser")
                .profileImage("friendImage.png")
                .build();
        userRepository.save(receiverUser);

        User requesterUser = User.builder()
                .nickname("requesterUser")
                .profileImage("requesterImage.png")
                .build();
        userRepository.save(requesterUser);

        // 친구 요청 생성 후 수락
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requesterUser)
                .receiver(receiverUser)
                .status(FriendRequest.Status.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);

        // 친구 요청 수락
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        // 이미 수락된 요청을 다시 수락하려고 시도
        mockMvc.perform(post("/api/friends/requests/{requestId}/accept", friendRequest.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 수락하거나 거절한 사용자 입니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_거절_실패_이미_수락된_요청() throws Exception {
        // 이미 수락된 친구 요청을 시도
        User receiverUser = User.builder()
                .nickname("friendUser")
                .profileImage("friendImage.png")
                .build();
        userRepository.save(receiverUser);

        User requesterUser = User.builder()
                .nickname("requesterUser")
                .profileImage("requesterImage.png")
                .build();
        userRepository.save(requesterUser);

        // 친구 요청 생성 후 수락
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requesterUser)
                .receiver(receiverUser)
                .status(FriendRequest.Status.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);

        // 친구 요청 수락
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        // 이미 수락된 요청을 다시 거절하려고 시도
        mockMvc.perform(post("/api/friends/requests/{requestId}/reject", friendRequest.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 수락하거나 거절한 사용자 입니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_취소_성공() throws Exception {
        // 친구 요청 생성 (보낸 요청)
        User requester = userRepository.findById(userId).orElseThrow();
        User receiver = User.builder()
                .nickname("receiver")
                .profileImage("receiverImage.png")
                .build();
        userRepository.save(receiver);

        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequest.Status.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);

        // 친구 요청 취소
        mockMvc.perform(delete("/api/friends/requests/{requestId}/cancel", friendRequest.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 요청이 성공적으로 취소되었습니다."));

        mockMvc.perform(get("/api/friends/requests/sent")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0)); // 취소되었으므로 보낸 요청 목록에 없음
    }

    @Test
    @WithMockUser
    void 친구_요청_취소_실패_내가_보낸_요청_아님() throws Exception {
        // 친구 요청 생성 (다른 사용자가 보낸 요청)
        User requester = User.builder()
                .nickname("requester")
                .profileImage("requesterImage.png")
                .build();
        userRepository.save(requester);

        User receiver = userRepository.findById(userId).orElseThrow();
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequest.Status.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);

        // 친구 요청 취소 (다른 사용자가 보낸 요청을 내가 취소하려고 시도)
        mockMvc.perform(delete("/api/friends/requests/{requestId}/cancel", friendRequest.getId())
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_취소_실패_존재하지_않는_요청() throws Exception {
        // 존재하지 않는 요청 ID로 취소 시도
        mockMvc.perform(delete("/api/friends/requests/{requestId}/cancel", 999L)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 친구 요청을 찾을 수 없습니다."));
    }

    @Test
    @WithMockUser
    void 친구_요청_목록_조회_성공_받은_요청() throws Exception {
        mockMvc.perform(get("/api/friends/requests/received")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void 친구_요청_목록_조회_성공_보낸_요청() throws Exception {
        mockMvc.perform(get("/api/friends/requests/sent")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}