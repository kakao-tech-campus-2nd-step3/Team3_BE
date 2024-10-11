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
    void getFriends_성공() {
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
    void getFriendPlan_성공() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 0, 0);

        when(mockPlan.getId()).thenReturn(1L);
        when(mockPlan.getTitle()).thenReturn("Test Plan");
        when(mockPlan.getDescription()).thenReturn("This is a test plan.");
        when(mockPlan.getStartDate()).thenReturn(startDate); // LocalDateTime 반환
        when(mockPlan.getEndDate()).thenReturn(endDate);     // LocalDateTime 반환
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
    void getFriendPlan_플랜없음_예외발생() {
        // Arrange
        when(planRepository.findAllByUserIdAndAccessibility(1L, true)).thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            friendService.getFriendPlan(1L, 1L);
        });
        assertEquals(ErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
    }
}