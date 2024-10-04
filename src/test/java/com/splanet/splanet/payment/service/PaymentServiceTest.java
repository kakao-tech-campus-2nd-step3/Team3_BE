package com.splanet.splanet.payment.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.payment.dto.PaymentRequest;
import com.splanet.splanet.payment.dto.PaymentResponse;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.payment.repository.PaymentRepository;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private PaymentRepository paymentRepository;
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        paymentService = new PaymentService(paymentRepository, subscriptionRepository);
    }

    @Test
    void 결제생성성공() {
        // Given
        PaymentRequest request = PaymentRequest.builder()
                .subscriptionId(1L)
                .price(1000)
                .build();

        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(1L);
        when(subscriptionRepository.findById(request.getSubscriptionId())).thenReturn(Optional.of(subscription));

        Payment payment = Payment.builder()
                .subscription(subscription)
                .price(request.getPrice())
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment); // save 메서드에 대한 모킹 설정

        // When
        PaymentResponse response = paymentService.createPayment(1L, request);

        // Then
        assertNotNull(response);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제생성실패_존재하지않는구독() {
        // Given
        PaymentRequest request = mock(PaymentRequest.class);
        when(request.getSubscriptionId()).thenReturn(1L);
        when(subscriptionRepository.findById(request.getSubscriptionId())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.createPayment(1L, request));
    }

    @Test
    void 결제삭제성공() {
        // Given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When
        paymentService.deletePayment(1L);

        // Then
        verify(paymentRepository).delete(payment);
    }

    @Test
    void 결제삭제실패_존재하지않는결제() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.deletePayment(1L));
    }

    @Test
    void 결제상태조회성공() {
        // Given
        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(1L);

        Payment payment = Payment.builder()
                .id(1L)
                .subscription(subscription)
                .price(1000)
                .status(Payment.Status.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build(); // Payment 객체 생성

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When
        PaymentResponse response = paymentService.getPaymentStatus(1L);

        // Then
        assertNotNull(response);
    }

    @Test
    void 결제상태조회실패_존재하지않는결제() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.getPaymentStatus(1L));
    }
}