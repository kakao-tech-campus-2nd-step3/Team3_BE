package com.splanet.splanet.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        testUser = User.builder()
                .nickname("구독 테스트 사용자")
                .profileImage("http://example.com/profile.jpg")
                .kakaoId(123456789L)
                .isPremium(false)
                .build();
        userRepository.save(testUser);

        // JWT 토큰 생성
        token = "Bearer " + jwtTokenProvider.createAccessToken(testUser.getId());
    }

    @Test
    void 구독_생성_성공() throws Exception {
        // Given
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        // When & Then
        mockMvc.perform(post("/api/subscription/me/subscribe")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구독이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.subscription.id").exists())
                .andExpect(jsonPath("$.subscription.startDate").exists())
                .andExpect(jsonPath("$.subscription.endDate").doesNotExist());

        // 데이터베이스에 구독 정보가 저장되었는지 확인
        Subscription newSubscription = subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(testUser.getId(), Subscription.Status.ACTIVE)
                .orElseThrow(() -> new AssertionError("구독 정보가 저장되지 않았습니다."));

        assertThat(newSubscription.getType()).isEqualTo(request.getType());
        assertThat(newSubscription.getStatus()).isEqualTo(Subscription.Status.ACTIVE);
    }

    @Test
    void 구독_생성_실패_이미_구독중() throws Exception {
        // Given
        Subscription existingSubscription = Subscription.builder()
                .user(testUser)
                .status(Subscription.Status.ACTIVE)
                .type(Subscription.Type.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
        subscriptionRepository.save(existingSubscription);

        // When & Then
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        mockMvc.perform(post("/api/subscription/me/subscribe")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_SUBSCRIBED.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.ALREADY_SUBSCRIBED.getStatus().value()));
    }

    @Test
    void 구독_정보_조회_성공() throws Exception {
        // Given
        Subscription subscription = Subscription.builder()
                .user(testUser)
                .type(Subscription.Type.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
        subscriptionRepository.save(subscription);

        // When & Then
        mockMvc.perform(get("/api/subscription/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구독 정보가 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.subscription.id").exists())
                .andExpect(jsonPath("$.subscription.startDate").exists())
                .andExpect(jsonPath("$.subscription.endDate").exists());
    }

    @Test
    void 구독_정보_조회_실패_구독없음() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(get("/api/subscriptions/me")
                        .header("Authorization", "Bearer <valid-token>")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}