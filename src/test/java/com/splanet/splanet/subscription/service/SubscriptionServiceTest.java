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
import org.mockito.ArgumentCaptor;
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
        Subscription mockedSubscription = mock(Subscription.class);
        when(mockedSubscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(subscriptionRepository.findTopByUserIdAndStatusOrderByStartDateDesc(any(Long.class), any(Subscription.Status.class)))
                .thenReturn(Optional.of(mockedSubscription));

        // When
        ResponseEntity<String> response = subscriptionService.cancelSubscription(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("{\"message\": \"구독이 성공적으로 취소되었습니다.\"}", response.getBody());

        verify(mockedSubscription, times(1)).cancel(); // cancel 메소드가 호출되었는지 확인

        verify(subscriptionRepository, times(1)).save(mockedSubscription);

        when(mockedSubscription.getStatus()).thenReturn(Subscription.Status.CANCELED); // 상태를 CANCELED로 설정
        assertEquals(Subscription.Status.CANCELED, mockedSubscription.getStatus());
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
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockedUser));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(mockedSubscription);

        ResponseEntity<SubscriptionResponse> response = subscriptionService.subscribe(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("구독이 성공적으로 구매되었습니다.", response.getBody().getMessage());

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());
        Subscription savedSubscription = captor.getValue();
        assertEquals(mockedUser, savedSubscription.getUser()); // Compare with the mocked user
        assertEquals(Subscription.Status.ACTIVE, savedSubscription.getStatus());
    }

    @Test
    void 구독실패_사용자미발견() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setType(Subscription.Type.MONTHLY);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> subscriptionService.subscribe(1L, request));
    }
}