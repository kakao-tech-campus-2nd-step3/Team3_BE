//package com.splanet.splanet.subscription.service;
//
//import com.splanet.splanet.subscription.dao.SubscriptionDao;
//import com.splanet.splanet.subscription.dto.SubscriptionDto;
//import com.splanet.splanet.subscription.entity.Subscription;
//import com.splanet.splanet.core.exception.BusinessException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class SubscriptionServiceTest {
//
//    @Mock
//    private SubscriptionDao subscriptionDao;
//
//    @InjectMocks
//    private SubscriptionService subscriptionService;
//
//    private Long userId;
//    private Subscription subscription;
//    private SubscriptionDto subscriptionDto;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        userId = 1L;
//
//        // 모킹할 Subscription 객체
//        subscription = Subscription.builder()
//                .userId(userId)
//                .type(Subscription.Type.MONTHLY)
//                .status(Subscription.Status.ACTIVE)
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now().plusMonths(1))
//                .build();
//
//        // SubscriptionDto 객체 생성 (가정)
//        subscriptionDto = SubscriptionDto.fromSubscription(subscription);
//    }
//
//    @Test
//    void 구독조회성공() {
//        // given
//        when(subscriptionDao.findLatestActiveSubscription(userId)).thenReturn(Optional.of(subscription));
//
//        // when
//        ResponseEntity<SubscriptionDto> response = subscriptionService.getSubscription(userId);
//
//        // then
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals("구독 정보가 성공적으로 조회되었습니다.", response.getHeaders().getFirst("Message"));
//    }
//
//    @Test
//    void 구독조회실패_사용자미인증() {
//        // given
//        userId = null;
//
//        // when & then
//        assertThrows(BusinessException.class, () -> subscriptionService.getSubscription(userId));
//    }
//
//    @Test
//    void 구독취소성공() {
//        // given
//        when(subscriptionDao.findLatestActiveSubscription(userId)).thenReturn(Optional.of(subscription));
//
//        // when
//        ResponseEntity<String> response = subscriptionService.cancelSubscription(userId);
//
//        // then
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("구독이 성공적으로 취소되었습니다.", response.getBody());
//    }
//
//    @Test
//    void 구독취소실패_이미취소됨() {
//        // given
//        subscription.setStatus(Subscription.Status.CANCELED);
//        when(subscriptionDao.findLatestActiveSubscription(userId)).thenReturn(Optional.of(subscription));
//
//        // when & then
//        assertThrows(BusinessException.class, () -> subscriptionService.cancelSubscription(userId));
//    }
//
//    @Test
//    void 구독하기성공() {
//        // given
//        when(subscriptionDao.saveSubscription(any(Subscription.class))).thenReturn(subscription);
//
//        // when
//        ResponseEntity<SubscriptionDto> response = subscriptionService.subscribe(userId, Subscription.Type.MONTHLY);
//
//        // then
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals("구독이 성공적으로 구매되었습니다.", response.getHeaders().getFirst("Message"));
//    }
//}