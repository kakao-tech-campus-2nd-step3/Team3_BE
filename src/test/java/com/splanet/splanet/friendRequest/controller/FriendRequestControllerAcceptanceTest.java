package com.splanet.splanet.friendRequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.FriendRequestCreateRequest;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FriendRequestControllerAcceptanceTest {

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
    private Long requesterId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        User requester = User.builder()
                .nickname("requester")
                .profileImage("requesterImage.png")
                .build();
        userRepository.save(requester);
        requesterId = requester.getId();

        User receiver = User.builder()
                .nickname("receiver")
                .profileImage("receiverImage.png")
                .build();
        userRepository.save(receiver);
        receiverId = receiver.getId();

        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(requesterId);
    }

    @Test
    void 내가_친구_요청_보내면_친구가_수락하기() throws Exception {
        FriendRequestCreateRequest request = new FriendRequestCreateRequest(receiverId);
        mockMvc.perform(post("/api/friends/requests")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("친구 요청이 성공적으로 전송되었습니다."));

        List<FriendRequest> pendingRequests = friendRequestRepository.findPendingRequestsByReceiverId(receiverId, requesterId, FriendRequest.Status.PENDING);
        FriendRequest friendRequest = pendingRequests.get(0);

        String receiverAccessToken = "Bearer " + jwtTokenProvider.createAccessToken(receiverId);

        mockMvc.perform(post("/api/friends/requests/{requestId}/accept", friendRequest.getId())
                        .header("Authorization", receiverAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACCEPTED"));
    }
}