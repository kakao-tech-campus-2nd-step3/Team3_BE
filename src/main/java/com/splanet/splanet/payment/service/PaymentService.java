package com.splanet.splanet.payment.service;

import com.splanet.splanet.payment.dto.PaymentDto;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.payment.repository.PaymentRepository;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    // 결제 생성
    @Transactional
    public PaymentDto.Response createPayment(Long userId, PaymentDto.CreateRequest request) {
        // 구독 정보 조회
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구독입니다."));

        // 결제 객체 생성
        Payment payment = Payment.builder()
                .subscription(subscription)
                .price(request.getPrice())
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();

        // 결제 저장
        Payment savedPayment = paymentRepository.save(payment);

        // DTO로 변환하여 반환
        return PaymentDto.Response.fromPayment(savedPayment);
    }

    // 결제 삭제
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
        paymentRepository.delete(payment);
    }

    // 결제 상태 조회
    public PaymentDto.Response getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        return PaymentDto.Response.fromPayment(payment);
    }
}