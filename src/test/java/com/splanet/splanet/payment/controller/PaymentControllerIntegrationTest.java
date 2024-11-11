package com.splanet.splanet.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.payment.repository.PaymentRepository;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private User testUser;
    private String token;
    private Subscription testSubscription;
    private Payment testPayment;

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

        // 테스트용 Subscription 생성
        testSubscription = Subscription.builder()
                .user(testUser)
                .status(Subscription.Status.ACTIVE)
                .type(Subscription.Type.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
        subscriptionRepository.save(testSubscription);

        // Payment 생성
        testPayment = Payment.builder()
                .price(1000)
                .status(Payment.Status.PENDING)
                .paymentDate(LocalDateTime.now())
                .subscription(testSubscription)
                .build();
        paymentRepository.save(testPayment);
    }

    @Test
    public void 결제상태조회_존재하지않는_결제ID_실패() throws Exception {
        // given
        Long invalidPaymentId = 999L;

        // when & then
        mockMvc.perform(get("/api/payment/{paymentId}", invalidPaymentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("결제 정보가 존재하지 않습니다."))
                .andExpect(jsonPath("$.status").value("404"));
    }

    @Test
    public void 결제삭제_성공() throws Exception {
        // given
        Long validPaymentId = testPayment.getId();
        Long validUserId = testPayment.getSubscription().getUser().getId();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/payment/{paymentId}", validPaymentId)
                        .header("Authorization", token)
                        .param("userId", validUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("결제가 성공적으로 삭제되었습니다."));
    }

    @Test
    public void 결제삭제_결제없음_실패() throws Exception {
        // given
        Long invalidPaymentId = 999L;

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/payment/{paymentId}", invalidPaymentId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("결제 정보가 존재하지 않습니다."))
                .andExpect(jsonPath("$.status").value("404"));
    }

    @Test
    public void 결제삭제_다른유저의_결제정보접근_실패() throws Exception {
        Long validPaymentId = testPayment.getId();
        Long invalidUserId = 123L;
        String invalidToken = jwtTokenProvider.createAccessToken(invalidUserId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/payment/{paymentId}", validPaymentId)
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("다른 유저의 결제 정보에 접근할 수 없습니다."))
                .andExpect(jsonPath("$.status").value("401"));
    }
}