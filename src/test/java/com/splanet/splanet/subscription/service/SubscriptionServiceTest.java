package com.splanet.splanet.subscription.service;

import com.splanet.splanet.subscription.dto.SubscriptionRequest;
import com.splanet.splanet.subscription.dto.SubscriptionResponse;
import com.splanet.splanet.subscription.entity.Subscription;
import com.splanet.splanet.subscription.repository.SubscriptionRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    private User mockedUser;
    private Subscription mockedSubscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 모킹된 User 객체 생성
        mockedUser = mock(User.class);
        when(mockedUser.getId()).thenReturn(1L);

        // 모킹된 Subscription 객체 생성
        mockedSubscription = mock(Subscription.class);
        when(mockedSubscription.getUser()).thenReturn(mockedUser);
        when(mockedSubscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(mockedSubscription.getStartDate()).thenReturn(LocalDateTime.now());
        when(mockedSubscription.getEndDate()).thenReturn(LocalDateTime.now().plusMonths(1));
    }

    @Test
    void 구독조회성공() {
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(any(Long.class), any(Subscription.Status.class)))
                .thenReturn(Optional.of(mockedSubscription));

        ResponseEntity<SubscriptionResponse> response = subscriptionService.getSubscription(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("구독 정보가 성공적으로 조회되었습니다.", response.getBody().getMessage());
        assertNotNull(response.getBody().getSubscription());

        SubscriptionResponse.SubscriptionDetails details = response.getBody().getSubscription();
        assertEquals(mockedSubscription.getId(), details.getId());
        assertEquals(mockedSubscription.getStartDate().toString(), details.getStartDate());
        assertEquals(mockedSubscription.getEndDate().toString(), details.getEndDate());
    }

    @Test
    void 구독조회실패_사용자미인증() {
        assertThrows(BusinessException.class, () -> subscriptionService.getSubscription(null));
    }

    @Test
    void 구독조회실패_구독미발견() {
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(any(Long.class), any(Subscription.Status.class)))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> subscriptionService.getSubscription(1L));
    }

    @Test
    void 구독취소성공() {
        // Given
        Long userId = 1L;
        Subscription mockedSubscription = mock(Subscription.class);
        User mockedUser = mock(User.class); // User 객체 모킹

        when(mockedSubscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(any(Long.class), any(Subscription.Status.class)))
                .thenReturn(Optional.of(mockedSubscription));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser)); // 유저 조회 모킹

        // When
        ResponseEntity<String> response = subscriptionService.cancelSubscription(userId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("{\"message\": \"구독이 성공적으로 취소되었습니다.\"}", response.getBody());

        verify(mockedSubscription, times(1)).cancel(); // cancel 메소드가 호출되었는지 확인
        verify(subscriptionRepository, times(1)).delete(mockedSubscription); // 구독 삭제 호출 확인
        verify(userRepository, times(1)).save(mockedUser); // 유저 저장 호출 확인
        verify(mockedUser, times(1)).setIsPremium(false); // 프리미엄 상태가 false로 설정되었는지 확인
    }

    @Test
    void 구독취소실패_사용자미인증() {
        assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(null));
    }

    @Test
    void 구독취소실패_이미취소됨() {
        when(mockedSubscription.getStatus()).thenReturn(Subscription.Status.CANCELED);
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(any(Long.class), any(Subscription.Status.class)))
                .thenReturn(Optional.of(mockedSubscription));

        assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(1L));
    }

    @Test
    void 구독성공() {
        // Given
        User user = User.builder()
                .id(1L)
                .nickname("testNickname")
                .isPremium(false)
                .build();

        // 모킹 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Subscription subscription = Subscription.builder()
                .id(1L) // id는 테스트를 위해 수동으로 설정
                .user(user)
                .type(Subscription.Type.MONTHLY)
                .status(Subscription.Status.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        // save 메서드의 결과를 모킹
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        // When
        ResponseEntity<SubscriptionResponse> response = subscriptionService.subscribe(1L, request);

        // Then
        assertTrue(user.getIsPremium()); // isPremium이 true로 변경되었는지 확인
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getSubscription());
        assertEquals(subscription.getId(), response.getBody().getSubscription().getId()); // id 확인
    }

    @Test
    void 구독실패_사용자미발견() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> subscriptionService.subscribe(1L, request));
    }
}