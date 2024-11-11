package com.splanet.splanet.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.entity.Comment;
import com.splanet.splanet.comment.repository.CommentRepository;
import com.splanet.splanet.comment.service.CommentService;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
    void 댓글_조회_특정_유저() throws Exception {
        // testUser가 작성한 댓글 추가
        Comment comment = Comment.builder()
                .user(testUser)
                .writer(writerUser)
                .content("testUser의 댓글")
                .build();
        commentRepository.save(comment);

        // testUser의 댓글만 조회하는 API 요청
        mockMvc.perform(get("/api/comments/{userId}", testUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("testUser의 댓글"));
    }

    @Test
    void 댓글_조회_존재하지_않는_댓글() throws Exception {
        Long nonExistentCommentId = 999L;

        mockMvc.perform(get("/api/comments/{id}", nonExistentCommentId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void 댓글_작성_성공() throws Exception {
        // CommentRequest에서 userId와 content만 전달하도록 수정
        CommentRequest commentRequest = new CommentRequest(testUser.getId(), "새로운 댓글 내용");

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", token)
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
    void 댓글_작성_실패_존재하지않은_사용자ID() throws Exception {
        // 존재하지 않는 userId로 댓글 작성 요청
        Long invalidUserId = 999L;
        CommentRequest commentRequest = new CommentRequest(invalidUserId, "존재하지않는 userId로 댓글 작성 시도");

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("유저가 존재하지 않습니다."));
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        CommentRequest updatedRequest = new CommentRequest(testUser.getId(), "수정된 댓글 내용");

        mockMvc.perform(put("/api/comments/" + testComment.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 수정되었습니다."));

        // 댓글 내용이 수정되었는지 확인
        Comment updatedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글 내용");
    }

    @Test
    void 댓글_수정_실패_작성자가_아님() throws Exception {
        CommentRequest updatedRequest = new CommentRequest(testUser.getId(), "권한 없는 사용자의 수정 시도");

        String unauthorizedToken = "Bearer " + jwtTokenProvider.createAccessToken(testUser.getId());

        mockMvc.perform(put("/api/comments/" + testComment.getId())
                        .header("Authorization", unauthorizedToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("권한이 없습니다."));
    }

    @Test
    void 댓글_수정_실패_존재하지_않는_댓글() throws Exception {
        CommentRequest updatedRequest = new CommentRequest(testUser.getId(), "수정된 댓글 내용");

        // 존재하지 않는 댓글 ID로 수정 요청
        Long nonExistentCommentId = 999L;

        mockMvc.perform(put("/api/comments/" + nonExistentCommentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("댓글을 찾을 수 없습니다."));
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        mockMvc.perform(delete("/api/comments/" + testComment.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글이 성공적으로 삭제되었습니다."));

        // 댓글이 실제로 삭제되었는지 확인
        assertThat(commentRepository.findById(testComment.getId())).isEmpty();
    }

    @Test
    void 댓글_삭제_실패_존재하지_않는_댓글() throws Exception {
        // 존재하지 않는 댓글 ID로 삭제 요청
        Long nonExistentCommentId = 999L;

        mockMvc.perform(delete("/api/comments/" + nonExistentCommentId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("댓글을 찾을 수 없습니다."));
    }
}