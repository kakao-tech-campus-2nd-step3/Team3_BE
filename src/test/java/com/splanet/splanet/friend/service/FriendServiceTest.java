package com.splanet.splanet.friend.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private User mockUser;

    @Mock
    private Friend mockFriend;

    @Mock
    private Plan mockPlan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 친구목록조회_성공() {
        // Arrange
        when(mockFriend.getFriend()).thenReturn(mockUser);
        when(mockUser.getNickname()).thenReturn("testUser");
        when(mockUser.getProfileImage()).thenReturn("testProfileImageUrl");
        when(friendRepository.findByUserId(1L)).thenReturn(Collections.singletonList(mockFriend));

        // Act
        List<FriendResponse> friends = friendService.getFriends(1L);

        // Assert
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals("testUser", friends.get(0).nickname());
        assertEquals("testProfileImageUrl", friends.get(0).profileImage());
    }

    @Test
    void 친구플랜조회_성공() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 0, 0);

        // 친구 확인 모킹
        when(friendRepository.existsByUserIdAndFriendId(1L, 1L)).thenReturn(true);

        when(mockPlan.getId()).thenReturn(1L);
        when(mockPlan.getTitle()).thenReturn("Test Plan");
        when(mockPlan.getDescription()).thenReturn("This is a test plan.");
        when(mockPlan.getStartDate()).thenReturn(startDate);
        when(mockPlan.getEndDate()).thenReturn(endDate);
        when(mockPlan.getAccessibility()).thenReturn(true);
        when(mockPlan.getIsCompleted()).thenReturn(false);
        when(mockPlan.getCreatedAt()).thenReturn(null);
        when(mockPlan.getUpdatedAt()).thenReturn(null);
        when(planRepository.findAllByUserIdAndAccessibility(1L, true)).thenReturn(Collections.singletonList(mockPlan));

        // Act
        ResponseEntity<List<PlanResponseDto>> response = friendService.getFriendPlan(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Plan", response.getBody().get(0).getTitle());
    }

    @Test
    void 친구플랜조회_성공_플랜없음() {
        // Arrange
        when(friendRepository.existsByUserIdAndFriendId(1L, 1L)).thenReturn(true); // 친구 확인 성공
        when(planRepository.findAllByUserIdAndAccessibility(1L, true)).thenReturn(Collections.emptyList()); // 플랜 없음

        // Act
        ResponseEntity<List<PlanResponseDto>> response = friendService.getFriendPlan(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue()); // 성공적으로 빈 목록을 반환해야 함
        assertTrue(response.getBody().isEmpty()); // 빈 목록인지 확인
    }

    @Test
    void 친구플랜조회_실패_친구아님() {
        // Arrange
        when(friendRepository.existsByUserIdAndFriendId(1L, 2L)).thenReturn(false); // 친구가 아닌 경우

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            friendService.getFriendPlan(2L, 1L); // 다른 userId로 조회
        });
        assertEquals(ErrorCode.FRIEND_NOT_FOUND, exception.getErrorCode());
    }
}