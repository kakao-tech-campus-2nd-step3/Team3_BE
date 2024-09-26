package com.splanet.splanet.subscription.controller;

import com.splanet.splanet.subscription.dto.SubscriptionDto;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.service.SubscriptionService;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 구독성공() {
        Long userId = 1L;
        SubscriptionDto mockDto = SubscriptionDto.builder().build();
        when(subscriptionService.getSubscription(userId)).thenReturn(ResponseEntity.ok(mockDto));

        ResponseEntity<SubscriptionDto> response = subscriptionController.getSubscription(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockDto, response.getBody());
    }

    @Test
    void 구독실패() {
        Long userId = 1L;
        when(subscriptionService.getSubscription(userId)).thenThrow(new BusinessException(ErrorCode.SUBSCRIPSTION_NOT_FOUND));

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionController.getSubscription(userId));
        assertEquals("활성화된 구독을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 구독취소성공() {
        Long userId = 1L;
        when(subscriptionService.cancelSubscription(userId)).thenReturn(ResponseEntity.ok("구독이 성공적으로 취소되었습니다."));

        ResponseEntity<String> response = subscriptionController.cancelSubscription(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("구독이 성공적으로 취소되었습니다.", response.getBody());
    }

    @Test
    void 구독취소실패() {
        Long userId = 1L;
        when(subscriptionService.cancelSubscription(userId)).thenThrow(new BusinessException(ErrorCode.ALREADY_CANCELED));

        BusinessException exception = assertThrows(BusinessException.class, () -> subscriptionController.cancelSubscription(userId));
        assertEquals("이미 취소된 구독입니다.", exception.getMessage()); // ErrorCode 메시지와 일치해야 함
    }

    @Test
    void 구독하기성공() {
        Long userId = 1L;
        SubscriptionDto mockDto = SubscriptionDto.builder().build();
        when(subscriptionService.subscribe(userId, Subscription.Type.MONTHLY)).thenReturn(ResponseEntity.ok(mockDto));

        ResponseEntity<SubscriptionDto> response = subscriptionController.subscribe(userId, Subscription.Type.MONTHLY);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockDto, response.getBody());
    }
}