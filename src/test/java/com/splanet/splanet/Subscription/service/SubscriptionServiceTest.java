package com.splanet.splanet.Subscription.service;

import com.splanet.splanet.Subscription.dao.SubscriptionDao;
import com.splanet.splanet.Subscription.dto.SubscriptionDto;
import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 구독 조회 성공 케이스
    @Test
    void 구독성공() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.of(subscription));

        ResponseEntity<SubscriptionDto> response = subscriptionService.getSubscription(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("구독 정보가 성공적으로 조회되었습니다.", response.getHeaders().getFirst("Message"));
    }

    // 구독 조회 실패: 인증되지 않은 사용자
    @Test
    void 구독실패인증되지않음() {
        Long userId = null;

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionService.getSubscription(userId));
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    // 구독 조회 실패: 활성화된 구독이 없음
    @Test
    void 구독실패구독없음() {
        Long userId = 1L;
        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionService.getSubscription(userId));
        assertEquals(ErrorCode.SUBSCRIPSTION_NOT_FOUND, exception.getErrorCode());
    }

    // 구독 취소 성공 케이스
    @Test
    void 구독취소성공() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.of(subscription));

        ResponseEntity<String> response = subscriptionService.cancelSubscription(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("구독이 성공적으로 취소되었습니다.", response.getBody());
        verify(subscriptionDao).cancelSubscription(subscription);
    }

    // 구독 취소 실패: 인증되지 않은 사용자
    @Test
    void 구독취소실패인증되지않음() {
        Long userId = null;

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(userId));
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    // 구독 취소 실패: 활성화된 구독이 없음
    @Test
    void 구독취소실패구독없음() {
        Long userId = 1L;
        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(userId));
        assertEquals(ErrorCode.SUBSCRIPSTION_NOT_FOUND, exception.getErrorCode());
    }

    // 구독 취소 실패: 이미 취소된 구독
    @Test
    void 구독취소실패이미취소됨() {
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.CANCELED) // 이미 취소된 상태
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.of(subscription));

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(userId));
        assertEquals(ErrorCode.ALREADY_CANCELED, exception.getErrorCode());
    }

    // 구독하기 성공 케이스
    @Test
    void 구독하기성공() {
        Long userId = 1L;
        Subscription.Type type = Subscription.Type.MONTHLY;
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionDao.saveSubscription(any(Subscription.class))).thenReturn(subscription);

        ResponseEntity<SubscriptionDto> response = subscriptionService.subscribe(userId, type);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("구독이 성공적으로 구매되었습니다.", response.getBody().getMessage());
    }
}