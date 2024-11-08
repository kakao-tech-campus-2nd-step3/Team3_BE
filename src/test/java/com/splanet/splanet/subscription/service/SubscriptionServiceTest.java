package com.splanet.splanet.subscription.service;

import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .nickname("테스트유저")
                .isPremium(false)
                .build();
    }

    @Test
    public void 구독하기_성공() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserIdAndStatus(1L, Subscription.Status.ACTIVE)).thenReturn(List.of());

        // when
        SubscriptionResponse response = subscriptionService.subscribe(1L, Subscription.Type.MONTHLY).getBody();

        // then
        assertNotNull(response);
        assertTrue(response.getSubscription() != null);
        assertEquals("구독이 성공적으로 완료되었습니다.", response.getMessage());

        verify(userRepository).save(argThat(savedUser -> savedUser.getIsPremium() == true));
    }

    @Test
    public void 구독하기_이미_활성화된_구독이_존재하는_경우() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Subscription existingSubscription = Subscription.builder()
                .user(user)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .build();

        when(subscriptionRepository.findByUserIdAndStatus(1L, Subscription.Status.ACTIVE))
                .thenReturn(List.of(existingSubscription));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.subscribe(1L, Subscription.Type.MONTHLY);
        });

        assertEquals(ErrorCode.ALREADY_SUBSCRIBED, exception.getErrorCode());
    }

    @Test
    public void 구독취소_성공() {
        // given
        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .status(Subscription.Status.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(1L, Subscription.Status.ACTIVE)).thenReturn(Optional.of(subscription));

        // when
        ResponseEntity<String> response = subscriptionService.cancelSubscription(1L);

        // then
        assertEquals("{\"message\": \"구독이 성공적으로 취소되었습니다.\"}", response.getBody());
        assertFalse(user.getIsPremium());
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    public void 구독취소_이미_취소된_구독인_경우() {
        // given
        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .status(Subscription.Status.CANCELED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(1L, Subscription.Status.ACTIVE)).thenReturn(Optional.of(subscription));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.cancelSubscription(1L);
        });
        assertEquals(ErrorCode.ALREADY_CANCELED, exception.getErrorCode());
    }

    @Test
    public void 구독조회_성공() {
        // given
        User user = User.builder().id(1L).nickname("testUser").isPremium(true).build();
        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(1L, Subscription.Status.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // when
        SubscriptionResponse response = subscriptionService.getSubscription(1L).getBody();

        // then
        assertNotNull(response);
        assertNotNull(response.getSubscription());
        assertEquals(subscription.getId(), response.getSubscription().getId());
    }

    @Test
    public void 구독조회_구독이_없는_경우() {
        // given
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(1L, Subscription.Status.ACTIVE)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.getSubscription(1L);
        });
        assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 사용자ID_검증() {
        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            subscriptionService.getSubscription(null);
        });
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }
}