package com.splanet.splanet.payment.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.payment.dto.PaymentResponse;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.payment.repository.PaymentRepository;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private Subscription subscription;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nickname("testUser")
                .build();

        subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .status(Subscription.Status.ACTIVE)
                .type(Subscription.Type.MONTHLY)
                .build();

        payment = Payment.builder()
                .id(1L)
                .subscription(subscription)
                .price(1000)
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();
    }

    @Test
    void 결제_조회_성공() {
        // When
        PaymentResponse paymentResponse = PaymentResponse.fromPayment(payment);

        // Then
        assertEquals(payment.getId(), paymentResponse.id());
        assertEquals(payment.getSubscription().getId(), paymentResponse.subscriptionId());
        assertEquals(payment.getPrice(), paymentResponse.price());
        assertEquals("COMPLETED", paymentResponse.status());
        assertEquals(payment.getPaymentDate(), paymentResponse.paymentDate());
        assertEquals(payment.getCreatedAt(), paymentResponse.createdAt());
    }

    @Test
    void 결제_조회_실패_존재하지_않는_결제() {
        // Given
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.getPaymentStatus(paymentId));
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 결제_생성_실패_구독_없음() {
        // Given
        Long userId = 1L;
        Long subscriptionId = 1L;
        int price = 1000;

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.createPayment(userId, subscriptionId, price));
        assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 결제_생성_실패_권한_없음() {
        // Given
        Long userId = 2L;
        Long subscriptionId = 1L;
        int price = 1000;

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.createPayment(userId, subscriptionId, price));
        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
    }

    @Test
    void 결제_삭제_시_결제정보가_없으면_예외() {
        // Given
        Long userId = 1L;
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.deletePayment(userId, paymentId));
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 결제_삭제_성공() {
        // Given
        Long userId = 1L;
        Long paymentId = 1L;

        User user = User.builder()
                .id(userId)
                .nickname("testUser")
                .build();

        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .status(Subscription.Status.ACTIVE)
                .type(Subscription.Type.MONTHLY)
                .build();

        Payment payment = Payment.builder()
                .id(paymentId)
                .subscription(subscription)
                .price(1000)
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();

        // Mock repository methods
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        doNothing().when(paymentRepository).delete(payment);

        // When
        paymentService.deletePayment(userId, paymentId);

        // Then
        verify(paymentRepository).delete(payment);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void 결제_생성_성공() {
        // Given
        Long userId = 1L;
        Long subscriptionId = 1L;
        int price = 1000;

        User user = User.builder()
                .id(userId)
                .nickname("testUser")
                .build();

        Subscription subscription = Subscription.builder()
                .id(subscriptionId)
                .user(user)
                .type(Subscription.Type.MONTHLY)
                .build();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            return new Payment(subscription, Payment.Status.COMPLETED, price, LocalDateTime.now());
        });

        // When
        PaymentResponse response = paymentService.createPayment(userId, subscriptionId, price);

        // Then
        assertEquals("COMPLETED", response.status());
        assertEquals(price, response.price());
        assertEquals(subscriptionId, response.subscriptionId());
    }
}