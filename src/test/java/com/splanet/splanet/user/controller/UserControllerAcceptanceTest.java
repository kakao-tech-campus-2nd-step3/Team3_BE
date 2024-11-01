package com.splanet.splanet.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.ErrorCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerAcceptanceTest {

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
    void 유저_생성_후_정보_조회_성공() throws Exception {
        // 유저 생성
        String nickname = "새로운유저";
        String profileImage = "http://example.com/new_profile.jpg";
        Boolean isPremium = true;

        mockMvc.perform(post("/api/users/create")
                        .param("nickname", nickname)
                        .param("profileImage", profileImage)
                        .param("isPremium", isPremium.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.profileImage").value(profileImage))
                .andExpect(jsonPath("$.isPremium").value(isPremium));

        // 유저 정보 조회
        User newUser = userRepository.findByNickname(nickname).orElseThrow();
        String newToken = "Bearer " + jwtTokenProvider.createAccessToken(newUser.getId());

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.profileImage").value(profileImage))
                .andExpect(jsonPath("$.isPremium").value(isPremium));
    }

    @Test
    void 유저_생성_후_정보_수정_및_조회_성공() throws Exception {
        // 유저 생성
        String nickname = "새로운유저";
        String profileImage = "http://example.com/new_profile.jpg";
        Boolean isPremium = true;

        mockMvc.perform(post("/api/users/create")
                        .param("nickname", nickname)
                        .param("profileImage", profileImage)
                        .param("isPremium", isPremium.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // 유저 정보 조회 및 토큰 생성
        User newUser = userRepository.findByNickname(nickname).orElseThrow();
        String newToken = "Bearer " + jwtTokenProvider.createAccessToken(newUser.getId());

        // 유저 정보 수정
        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .nickname("수정된닉네임")
                .profileImage("http://example.com/updated_profile.jpg")
                .isPremium(false)
                .build();

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(updateRequest.getNickname()))
                .andExpect(jsonPath("$.profileImage").value(updateRequest.getProfileImage()))
                .andExpect(jsonPath("$.isPremium").value(updateRequest.getIsPremium()));

        // 수정된 정보로 다시 조회
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(updateRequest.getNickname()))
                .andExpect(jsonPath("$.profileImage").value(updateRequest.getProfileImage()))
                .andExpect(jsonPath("$.isPremium").value(updateRequest.getIsPremium()));
    }

    @Test
    void 유저_생성_후_정보_조회_삭제_조회실패() throws Exception {
        // 유저 생성
        String nickname = "삭제할유저";
        String profileImage = "http://example.com/delete_profile.jpg";
        Boolean isPremium = false;

        mockMvc.perform(post("/api/users/create")
                        .param("nickname", nickname)
                        .param("profileImage", profileImage)
                        .param("isPremium", isPremium.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // 유저 정보 조회 및 토큰 생성
        User newUser = userRepository.findByNickname(nickname).orElseThrow();
        String newToken = "Bearer " + jwtTokenProvider.createAccessToken(newUser.getId());

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.profileImage").value(profileImage))
                .andExpect(jsonPath("$.isPremium").value(isPremium));

        // 유저 삭제
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 유저 정보 조회 실패 확인
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", newToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }
}
