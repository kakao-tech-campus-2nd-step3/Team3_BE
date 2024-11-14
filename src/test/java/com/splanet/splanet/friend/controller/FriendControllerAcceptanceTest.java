package com.splanet.splanet.friend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.entity.Comment;
import com.splanet.splanet.comment.repository.CommentRepository;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FriendControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userAToken;
    private String userBToken;
    private Long userAId;
    private Long userBId;
    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = User.builder()
                .nickname("testUser")
                .kakaoId(123456789L)
                .profileImage("http://example.com/profile.jpg")
                .isPremium(false)
                .build();
        userB = User.builder()
                .nickname("testWriter")
                .kakaoId(987654321L)
                .profileImage("http://example.com/profile.jpg")
                .isPremium(false)
                .build();

        userRepository.saveAll(List.of(userA, userB));

        userAId = userA.getId();
        userBId = userB.getId();

        userAToken = jwtTokenProvider.createAccessToken(userAId);
        userBToken = jwtTokenProvider.createAccessToken(userBId);

        FriendRequest friendRequest = new FriendRequest(userA, userB, FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);
   }

    @Test
    @WithMockUser
    void 친구_플랜조회_후_댓글작성() throws Exception {
        PlanRequestDto requestDto = PlanRequestDto.builder()
                .title("테스트 플랜")
                .description("테스트 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        String planResponse = mockMvc.perform(post("/api/plans")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 플랜"))
                .andExpect(jsonPath("$.description").value("테스트 설명"))
                .andReturn().getResponse().getContentAsString();

        Long planId = objectMapper.readValue(planResponse, PlanResponseDto.class).getId();

        mockMvc.perform(get("/api/plans/" + planId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 플랜"))
                .andExpect(jsonPath("$.description").value("테스트 설명"));

        CommentRequest commentRequest = new CommentRequest(userAId, "테스트 댓글");

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 작성되었습니다."));

        List<Comment> comments = commentRepository.findByUserId(userAId);
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("테스트 댓글");
    }
}