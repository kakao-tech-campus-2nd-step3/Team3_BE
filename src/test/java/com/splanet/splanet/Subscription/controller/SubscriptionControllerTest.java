package com.splanet.splanet.Subscription.controller;

import com.splanet.splanet.Subscription.entity.Subscription;
import com.splanet.splanet.Subscription.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionControllerTest {

    private SubscriptionService subscriptionService;
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        subscriptionService = mock(SubscriptionService.class);
        subscriptionController = new SubscriptionController(subscriptionService);
    }

    @Test
    @DisplayName("구독 조회 성공")
    void testGetSubscription_Success() {
        Long userId = 1L;

        // 서비스 응답을 올바른 타입으로 모킹
        when(subscriptionService.getSubscription(userId)).thenReturn(ResponseEntity.ok(Map.of(
                "subscription_type", Subscription.Type.MONTHLY.name().toLowerCase(),
                "start_date", "2024-09-01T10:00:00Z",
                "end_date", "2024-10-01T10:00:00Z",
                "status", "active"
        )));

        ResponseEntity<Map<String, Object>> response = subscriptionController.getSubscription(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("active", response.getBody().get("status"));
        assertEquals("monthly", response.getBody().get("subscription_type"));
    }

    @Test
    @DisplayName("구독 조회 실패")
    void testGetSubscription_Failure() {
        Long userId = 1L;

        // 서비스가 예외를 던지도록 모킹
        when(subscriptionService.getSubscription(userId)).thenThrow(new IllegalArgumentException("구독 정보를 찾을 수 없습니다."));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionController.getSubscription(userId);
        });

        assertEquals("구독 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("구독 취소 성공")
    void testCancelSubscription_Success() {
        Long userId = 1L;

        // 메서드를 호출하는 것이므로 모킹이 필요 없음
        subscriptionController.cancelSubscription(userId);
        verify(subscriptionService).cancelSubscription(userId);
    }

    @Test
    @DisplayName("구독 취소 실패")
    void testCancelSubscription_Failure() {
        Long userId = 1L;

        // 서비스가 예외를 던지도록 모킹
        doThrow(new IllegalArgumentException("구독 정보를 찾을 수 없습니다.")).when(subscriptionService).cancelSubscription(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionController.cancelSubscription(userId);
        });

        assertEquals("구독 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("구독하기 성공")
    void testSubscribe_Success() {
        Long userId = 1L;
        Subscription.Type type = Subscription.Type.MONTHLY;

        // 서비스 응답 모킹
        Map<String, Object> mockResponse = Map.of(
                "message", "구독이 성공적으로 구매되었습니다.",
                "subscription", Map.of(
                        "id", 123L,
                        "start_date", "2024-09-01T10:00:00Z",
                        "end_date", "2024-10-01T10:00:00Z"
                )
        );

        when(subscriptionService.subscribe(userId, type)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = subscriptionController.subscribe(userId, type);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("구독이 성공적으로 구매되었습니다.", response.getBody().get("message"));
    }

    @Test
    @DisplayName("구독하기 실패")
    void testSubscribe_Failure() {
        Long userId = 1L;
        Subscription.Type type = Subscription.Type.MONTHLY;

        // 서비스가 예외를 던지도록 모킹
        when(subscriptionService.subscribe(userId, type)).thenThrow(new RuntimeException("구독 생성에 실패했습니다."));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subscriptionController.subscribe(userId, type);
        });

        assertEquals("구독 생성에 실패했습니다.", exception.getMessage());
    }
}