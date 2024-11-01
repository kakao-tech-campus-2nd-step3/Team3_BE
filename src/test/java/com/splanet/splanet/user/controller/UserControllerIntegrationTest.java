package com.splanet.splanet.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        testUser = User.builder()
                .nickname("테스트유저")
                .profileImage("http://example.com/profile.jpg")
                .kakaoId(123456789L)
                .isPremium(false)
                .build();
        userRepository.save(testUser);

        // JWT 토큰 생성
        token = "Bearer " + jwtTokenProvider.createAccessToken(testUser.getId());
    }

    @Test
    void 유저_정보_조회_성공() throws Exception {
        // given
        Long userId = testUser.getId();

        // when & then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()))
                .andExpect(jsonPath("$.profileImage").value(testUser.getProfileImage()))
                .andExpect(jsonPath("$.isPremium").value(testUser.getIsPremium()));
    }

    @Test
    void 유저_정보_조회_실패_유저없음() throws Exception {
        // given
        Long nonExistentUserId = 999L;
        String invalidToken = "Bearer " + jwtTokenProvider.createAccessToken(nonExistentUserId);

        // when & then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }

    @Test
    void 유저_정보_수정_성공() throws Exception {
        // given
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .nickname("수정된닉네임")
                .profileImage("http://example.com/updated_profile.jpg")
                .isPremium(true)
                .build();

        // when & then
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(updateRequest.getNickname()))
                .andExpect(jsonPath("$.profileImage").value(updateRequest.getProfileImage()))
                .andExpect(jsonPath("$.isPremium").value(updateRequest.getIsPremium()));

        // 데이터베이스의 유저 정보도 업데이트되었는지 확인
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo(updateRequest.getNickname());
        assertThat(updatedUser.getProfileImage()).isEqualTo(updateRequest.getProfileImage());
        assertThat(updatedUser.getIsPremium()).isEqualTo(updateRequest.getIsPremium());
    }

    @Test
    void 유저_정보_수정_실패_유저없음() throws Exception {
        // given
        Long nonExistentUserId = 999L;
        String invalidToken = "Bearer " + jwtTokenProvider.createAccessToken(nonExistentUserId);

        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .nickname("수정된닉네임")
                .profileImage("http://example.com/updated_profile.jpg")
                .isPremium(true)
                .build();

        // when & then
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }

    @Test
    void 유저_생성_성공() throws Exception {
        // given
        String nickname = "새로운유저";
        String profileImage = "http://example.com/new_profile.jpg";
        Boolean isPremium = true;

        // when & then
        mockMvc.perform(post("/api/users/create")
                        .param("nickname", nickname)
                        .param("profileImage", profileImage)
                        .param("isPremium", isPremium.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.profileImage").value(profileImage))
                .andExpect(jsonPath("$.isPremium").value(isPremium));

        // 데이터베이스에 유저가 생성되었는지 확인
        User newUser = userRepository.findByNickname(nickname).orElseThrow();
        assertThat(newUser.getNickname()).isEqualTo(nickname);
        assertThat(newUser.getProfileImage()).isEqualTo(profileImage);
        assertThat(newUser.getIsPremium()).isEqualTo(isPremium);
    }

    @Test
    void 유저_생성_실패_닉네임_중복() throws Exception {
        // given
        String duplicateNickname = testUser.getNickname(); // 이미 존재하는 닉네임

        // when & then
        mockMvc.perform(post("/api/users/create")
                        .param("nickname", duplicateNickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_NICKNAME.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.DUPLICATE_NICKNAME.getStatus().value()));
    }

    @Test
    void 유저_삭제_성공() throws Exception {
        // given
        Long userId = testUser.getId();

        // when & then
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 데이터베이스에서 유저가 삭제되었는지 확인
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    void 유저_삭제_실패_유저없음() throws Exception {
        // given
        Long nonExistentUserId = 999L;
        String invalidToken = "Bearer " + jwtTokenProvider.createAccessToken(nonExistentUserId);

        // when & then
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }
}