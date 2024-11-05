package com.splanet.splanet.payment.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.payment.dto.PaymentResponse;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.payment.repository.PaymentRepository;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    // 결제 상태 조회
    public PaymentResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return PaymentResponse.fromPayment(payment);
    }

    // 결제 생성
    @Transactional
    public PaymentResponse createPayment(Long userId, Long subscriptionId, int price) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Payment payment = Payment.builder()
                .subscription(subscription)
                .price(price)
                .paymentDate(LocalDateTime.now())
                .status(Payment.Status.COMPLETED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromPayment(savedPayment);
    }

    // 결제 삭제
    @Transactional
    public void deletePayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getSubscription().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        paymentRepository.deleteById(paymentId);
    }
}