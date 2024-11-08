package com.splanet.splanet.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.service.CommentService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    private String accessToken;
    private Long userId;
    private Long writerId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .nickname("testuser")
                .profileImage("testimage.png")
                .build();
        User writer = User.builder()
                .nickname("writer")
                .profileImage("writerimage.png")
                .build();
        userRepository.saveAll(List.of(user, writer));
        userId = user.getId();
        writerId = writer.getId();

        accessToken = "Bearer " + jwtTokenProvider.createAccessToken(writerId);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 자기 자신에게 댓글 작성")
    void 댓글_작성_실패_자기자신에게_댓글() throws Exception {
        CommentRequest request = new CommentRequest("자기 자신에게 댓글 작성");

        String sameUserToken = "Bearer " + jwtTokenProvider.createAccessToken(userId);

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", sameUserToken)
                        .param("writerId", String.valueOf(userId))
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("잘못된 요청입니다 (유효하지 않은 댓글 ID).")));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글 ID")
    void 댓글_수정_실패_존재하지않는_댓글() throws Exception {
        CommentRequest request = new CommentRequest("수정 실패 댓글");

        mockMvc.perform(put("/api/comments/9999")
                        .header("Authorization", accessToken)
                        .param("userId", String.valueOf(writerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("댓글을 찾을 수 없습니다.")));
    }
}