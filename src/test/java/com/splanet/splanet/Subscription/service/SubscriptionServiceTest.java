package com.splanet.splanet.Subscription.service;

import com.splanet.splanet.Subscription.dao.SubscriptionDao;
import com.splanet.splanet.Subscription.entity.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("구독 조회 성공")
    public void testGetSubscription_Success() {
        // Given
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .type(Subscription.Type.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(Subscription.Status.ACTIVE)
                .build();

        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.of(subscription));

        // When
        ResponseEntity<Map<String, Object>> response = subscriptionService.getSubscription(userId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("monthly", body.get("subscription_type"));
        assertNotNull(body.get("start_date"));
        assertNotNull(body.get("end_date"));
        assertEquals("active", body.get("status"));

        verify(subscriptionDao).findActiveSubscription(userId);
    }

    @Test
    @DisplayName("구독 조회 실패")
    public void testGetSubscription_NotFound() {
        // Given
        Long userId = 1L;
        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.getSubscription(userId);
        });
        assertEquals("구독 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("구독 취소 성공")
    public void testCancelSubscription_Success() {
        // Given
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .status(Subscription.Status.ACTIVE)
                .build();

        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.of(subscription));

        // When
        subscriptionService.cancelSubscription(userId);

        // Then
        verify(subscriptionDao).cancelSubscription(subscription);
    }

    @Test
    @DisplayName("구독 취소 실패")
    public void testCancelSubscription_NotFound() {
        // Given
        Long userId = 1L;
        when(subscriptionDao.findActiveSubscription(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionService.cancelSubscription(userId);
        });
        assertEquals("구독 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("구독하기 성공")
    public void testSubscribe_Success() {
        // Given
        Long userId = 1L;
        Subscription.Type type = Subscription.Type.MONTHLY;

        Subscription subscription = Subscription.builder()
                .id(1L)
                .userId(userId)
                .type(type)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionDao.saveSubscription(any(Subscription.class))).thenReturn(subscription);

        // When
        Map<String, Object> result = subscriptionService.subscribe(userId, type);

        // Then
        assertEquals("구독이 성공적으로 구매되었습니다.", result.get("message"));
        Map<String, Object> subscriptionInfo = (Map<String, Object>) result.get("subscription");
        assertEquals(1L, subscriptionInfo.get("id"));
        assertNotNull(subscriptionInfo.get("start_date"));
        assertNotNull(subscriptionInfo.get("end_date"));

        verify(subscriptionDao).saveSubscription(any(Subscription.class));
    }
}