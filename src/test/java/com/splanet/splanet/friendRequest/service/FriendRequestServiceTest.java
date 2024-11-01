package com.splanet.splanet.friendRequest.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.ReceivedFriendRequestResponse;
import com.splanet.splanet.friendRequest.dto.SentFriendRequestResponse;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendRequestService friendRequestService;

    private User requester;
    private User receiver;
    private FriendRequest friendRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requester = User.builder()
                .id(1L)
                .nickname("요청자")
                .profileImage("requester.png")
                .build();

        receiver = User.builder()
                .id(2L)
                .nickname("수락자")
                .profileImage("receiver.png")
                .build();

        friendRequest = FriendRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequest.Status.PENDING)
                .build();
    }

    @Test
    void 친구요청전송_성공() {
        Long userId = requester.getId();
        Long receiverId = receiver.getId();

        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(friendRepository.existsByUserIdAndFriendId(userId, receiverId)).thenReturn(false);

        friendRequestService.sendFriendRequest(userId, receiverId);

        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    void 친구요청전송_본인에게요청() {
        Long userId = requester.getId();

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.sendFriendRequest(userId, userId)
        );

        assertEquals(ErrorCode.SELF_FRIEND_REQUEST_NOT_ALLOWED, exception.getErrorCode());
    }

    @Test
    void 친구요청수락_요청없음() {
        Long requestId = 1L;

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.acceptFriendRequest(requestId, receiver.getId())
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 친구요청수락_요청자와수락자가동일() {
        Long requestId = friendRequest.getId();
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.acceptFriendRequest(requestId, requester.getId())
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_NOT_RECEIVER, exception.getErrorCode());
    }

    @Test
    void 친구요청거절_요청자와수락자가동일() {
        Long requestId = friendRequest.getId();
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.rejectFriendRequest(requestId, requester.getId())
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_NOT_RECEIVER, exception.getErrorCode());
    }

    @Test
    void 친구요청목록조회_받은요청() {
        Long userId = receiver.getId();

        when(friendRequestRepository.findByReceiverIdWithRequester(userId)).thenReturn(Arrays.asList(friendRequest));

        List<ReceivedFriendRequestResponse> responses = friendRequestService.getReceivedFriendRequests(userId);

        assertEquals(1, responses.size());
        assertEquals(requester.getId(), responses.get(0).requesterId());
    }

    @Test
    void 친구요청목록조회_보낸요청() {
        Long userId = requester.getId();

        when(friendRequestRepository.findByRequesterIdWithReceiver(userId)).thenReturn(Arrays.asList(friendRequest));

        List<SentFriendRequestResponse> responses = friendRequestService.getSentFriendRequests(userId);

        assertEquals(1, responses.size());
        assertEquals(receiver.getId(), responses.get(0).receiverId());
    }

    @Test
    void 친구요청목록조회_받은요청없음() {
        Long userId = receiver.getId();

        when(friendRequestRepository.findByReceiverIdWithRequester(userId)).thenReturn(Collections.emptyList());

        List<ReceivedFriendRequestResponse> responses = friendRequestService.getReceivedFriendRequests(userId);

        assertEquals(0, responses.size()); // 빈 목록인지 확인
    }

    @Test
    void 친구요청목록조회_보낸요청없음() {
        Long userId = requester.getId();

        when(friendRequestRepository.findByRequesterId(userId)).thenReturn(Collections.emptyList());

        List<SentFriendRequestResponse> responses = friendRequestService.getSentFriendRequests(userId);

        assertEquals(0, responses.size()); // 빈 목록인지 확인
    }

    @Test
    void 친구요청전송_수신자존재하지않음() {
        Long userId = requester.getId();
        Long receiverId = 999L;

        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.sendFriendRequest(userId, receiverId)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 친구요청전송_이미친구인경우() {
        Long userId = requester.getId();
        Long receiverId = receiver.getId();

        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(friendRepository.existsByUserIdAndFriendId(userId, receiverId)).thenReturn(true); // 이미 친구인 경우

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.sendFriendRequest(userId, receiverId)
        );

        assertEquals(ErrorCode.FRIEND_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void 친구요청수락_이미수락된요청() {
        Long requestId = friendRequest.getId();
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.acceptFriendRequest(requestId, receiver.getId())
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED_OR_REJECTED, exception.getErrorCode());
    }

    @Test
    void 친구요청수락_이미거절된요청() {
        Long requestId = friendRequest.getId();
        friendRequest.setStatus(FriendRequest.Status.REJECTED);

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.acceptFriendRequest(requestId, receiver.getId())
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED_OR_REJECTED, exception.getErrorCode());
    }

    @Test
    void 친구요청취소_성공() {
        Long requestId = friendRequest.getId();
        Long userId = requester.getId();

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        friendRequestService.cancelFriendRequest(requestId, userId);

        verify(friendRequestRepository, times(1)).delete(friendRequest);
    }

    @Test
    void 친구요청취소_권한없음() {
        Long requestId = friendRequest.getId();
        Long userId = 999L; // 요청자가 아닌 다른 사용자의 ID

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.cancelFriendRequest(requestId, userId)
        );

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }
}