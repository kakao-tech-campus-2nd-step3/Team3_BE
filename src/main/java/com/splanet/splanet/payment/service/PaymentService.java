package com.splanet.splanet.payment.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.payment.dto.PaymentRequest;
import com.splanet.splanet.payment.dto.PaymentResponse;
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
    public PaymentResponse createPayment(Long userId, PaymentRequest request) {
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        Payment payment = Payment.builder()
                .subscription(subscription)
                .price(request.getPrice())
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.fromPayment(savedPayment);
    }

    // 결제 삭제
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        paymentRepository.delete(payment);
    }

    // 결제 상태 조회
    public PaymentResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return PaymentResponse.fromPayment(payment);
    }
}