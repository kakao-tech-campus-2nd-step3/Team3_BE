package com.splanet.splanet.subscription.service;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
        subscriptionService = new SubscriptionService(subscriptionRepository);
    }

    @Test
    void 구독조회성공() {
        Long userId = 1L;
        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(123L);
        when(subscription.getType()).thenReturn(Subscription.Type.MONTHLY);
        when(subscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(subscription.getStartDate()).thenReturn(LocalDateTime.now());
        when(subscription.getEndDate()).thenReturn(LocalDateTime.now().plusMonths(1));

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE))
                .thenReturn(java.util.Optional.of(subscription));

        SubscriptionResponse response = subscriptionService.getSubscription(userId).getBody();

        assertNotNull(response);
        assertEquals("구독 정보가 성공적으로 조회되었습니다.", response.getMessage());
        assertEquals(123L, response.getSubscription().getId());
    }

    @Test
    void 구독조회실패_구독미존재() {
        Long userId = 1L;

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE))
                .thenReturn(java.util.Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.getSubscription(userId);
        });

        assertEquals(ErrorCode.SUBSCRIPSTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 구독취소성공() {
        Long userId = 1L;
        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(123L);
        when(subscription.getUserId()).thenReturn(userId);
        when(subscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE))
                .thenReturn(java.util.Optional.of(subscription));

        subscriptionService.cancelSubscription(userId);

        verify(subscriptionRepository, times(1)).save(subscription);
        verify(subscription).cancel();  // cancel 메소드 호출 확인
    }

    @Test
    void 구독취소실패_이미취소됨() {
        Long userId = 1L;
        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(123L);
        when(subscription.getUserId()).thenReturn(userId);
        when(subscription.getStatus()).thenReturn(Subscription.Status.CANCELED);

        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(userId, Subscription.Status.ACTIVE))
                .thenReturn(java.util.Optional.of(subscription));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.cancelSubscription(userId);
        });

        assertEquals(ErrorCode.ALREADY_CANCELED, exception.getErrorCode());
    }

    @Test
    void 구독하기성공() {
        Long userId = 1L;
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        Subscription subscription = mock(Subscription.class);
        when(subscription.getId()).thenReturn(123L);
        when(subscription.getUserId()).thenReturn(userId);
        when(subscription.getType()).thenReturn(Subscription.Type.MONTHLY);
        when(subscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(subscription.getStartDate()).thenReturn(LocalDateTime.now());
        when(subscription.getEndDate()).thenReturn(LocalDateTime.now().plusMonths(1));

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.subscribe(userId, request).getBody();

        assertNotNull(response);
        assertEquals("구독이 성공적으로 구매되었습니다.", response.getMessage());
        assertEquals(123L, response.getSubscription().getId());
    }
}