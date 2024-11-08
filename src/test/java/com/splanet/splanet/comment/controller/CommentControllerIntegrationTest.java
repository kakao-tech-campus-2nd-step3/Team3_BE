package com.splanet.splanet.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.entity.Comment;
import com.splanet.splanet.comment.repository.CommentRepository;
import com.splanet.splanet.comment.service.CommentService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private User writerUser;
    private Comment testComment;
    private String token;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성 및 저장
        testUser = User.builder()
                .nickname("testUser")
                .kakaoId(123456789L)
                .profileImage("http://example.com/profile.jpg")
                .isPremium(false)
                .build();
        writerUser = User.builder()
                .nickname("testWriter")
                .kakaoId(987654321L)
                .profileImage("http://example.com/profile.jpg")
                .isPremium(false)
                .build();

        userRepository.saveAll(List.of(testUser, writerUser));

        // 댓글 생성 및 저장
        testComment = Comment.builder()
                .user(writerUser)
                .writer(writerUser)
                .content("기존 댓글 내용")
                .build();
        commentRepository.save(testComment);

        // 테스트용 JWT 생성 (writerUser로 설정하여 댓글 작성자 권한 부여)
        token = "Bearer " + jwtTokenProvider.createAccessToken(writerUser.getId());
    }

    @Test
    void 댓글_조회_성공() throws Exception {
        Comment comment = Comment.builder()
                .user(testUser)
                .writer(writerUser)
                .content("testUser의 댓글")
                .build();
        commentRepository.save(comment);

        mockMvc.perform(get("/api/comments/{userId}", testUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());

        List<CommentResponse> comments = commentService.getComments(testUser.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).content()).isEqualTo("testUser의 댓글");
    }

    @Test
    void 댓글_작성_성공() throws Exception {
        CommentRequest commentRequest = new CommentRequest("새로운 댓글 내용");

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", token)
                        .param("writerId", String.valueOf(writerUser.getId()))
                        .param("userId", String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 작성되었습니다."));

        // 댓글이 실제로 저장되었는지 확인
        List<Comment> comments = commentRepository.findByUserId(testUser.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("새로운 댓글 내용");
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        CommentRequest updatedRequest = new CommentRequest("수정된 댓글 내용");

        mockMvc.perform(put("/api/comments/" + testComment.getId())
                        .header("Authorization", token)
                        .param("userId", String.valueOf(writerUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 수정되었습니다."));

        // 댓글 내용이 수정되었는지 확인
        Comment updatedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글 내용");
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        mockMvc.perform(delete("/api/comments/" + testComment.getId())
                        .header("Authorization", token)
                        .param("userId", String.valueOf(writerUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 삭제되었습니다."));

        // 댓글이 실제로 삭제되었는지 확인
        assertThat(commentRepository.findById(testComment.getId())).isEmpty();
    }
}