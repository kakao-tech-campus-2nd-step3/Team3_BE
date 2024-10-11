package com.splanet.splanet.friendRequest.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.FriendRequestResponse;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendRequestService friendRequestService;

    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requester = User.builder()
                .id(1L)
                .nickname("requester")
                .profileImage("profileImageUrl")
                .build();
        receiver = User.builder()
                .id(2L)
                .nickname("receiver")
                .profileImage("profileImageUrl")
                .build();
    }

    @Test
    void 친구요청전송_성공() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        friendRequestService.sendFriendRequest(requester.getId(), receiver.getId());

        ArgumentCaptor<FriendRequest> captor = ArgumentCaptor.forClass(FriendRequest.class);
        verify(friendRequestRepository).save(captor.capture());
        FriendRequest savedRequest = captor.getValue();

        assertEquals(requester, savedRequest.getRequester());
        assertEquals(receiver, savedRequest.getReceiver());
        assertEquals(FriendRequest.Status.PENDING, savedRequest.getStatus());
    }

    @Test
    void 친구요청수락_성공() {
        // Mock 객체 생성 및 설정
        User requester = mock(User.class);
        when(requester.getId()).thenReturn(1L);
        when(requester.getNickname()).thenReturn("requester");
        when(requester.getProfileImage()).thenReturn("profileImageUrl");

        User receiver = mock(User.class);
        when(receiver.getId()).thenReturn(2L);
        when(receiver.getNickname()).thenReturn("receiver");
        when(receiver.getProfileImage()).thenReturn("profileImageUrl");

        // FriendRequest 객체를 모킹
        FriendRequest friendRequest = mock(FriendRequest.class);
        when(friendRequest.getRequester()).thenReturn(requester);
        when(friendRequest.getReceiver()).thenReturn(receiver);
        when(friendRequest.getStatus()).thenReturn(FriendRequest.Status.PENDING);

        // ID와 상태를 모킹
        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(friendRequest));

        // acceptFriendRequest 메서드에서 상태를 변경하도록 모킹
        doAnswer(invocation -> {
            // 친구 요청 상태를 ACCEPTED로 설정
            when(friendRequest.getStatus()).thenReturn(FriendRequest.Status.ACCEPTED);
            return null;
        }).when(friendRequestService).acceptFriendRequest(1L);

        // 메서드 호출
        FriendRequestResponse response = friendRequestService.acceptFriendRequest(1L);

        // Assertion
        assertNotNull(response);
        assertEquals(1L, response.getId()); // 여기서 null이 아니어야 함
        assertEquals(1L, response.getRequesterId());
        assertEquals("requester", response.getRequesterName());
        assertEquals(FriendRequest.Status.ACCEPTED.name(), response.getStatus());
        assertEquals("profileImageUrl", response.getProfileImage());
    }

    @Test
    void 받은요청조회_성공() {
        FriendRequest friendRequest = new FriendRequest(requester, receiver, FriendRequest.Status.PENDING);
        when(friendRequestRepository.findByReceiverId(receiver.getId())).thenReturn(Arrays.asList(friendRequest));

        var responses = friendRequestService.getReceivedFriendRequests(receiver.getId());

        assertEquals(1, responses.size());
        assertEquals("requester", responses.get(0).getRequesterName());
    }

    @Test
    void 보낸요청조회_성공() {
        FriendRequest friendRequest = new FriendRequest(requester, receiver, FriendRequest.Status.PENDING);
        when(friendRequestRepository.findByRequesterId(requester.getId())).thenReturn(Arrays.asList(friendRequest));

        var responses = friendRequestService.getSentFriendRequests(requester.getId());

        assertEquals(1, responses.size());
        assertEquals("receiver", responses.get(0).getRequesterName());
    }

    @Test
    void 친구요청전송_사용자미발견() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.sendFriendRequest(requester.getId(), receiver.getId())
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 친구요청수락_요청미발견() {
        when(friendRequestRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                friendRequestService.acceptFriendRequest(1L)
        );

        assertEquals(ErrorCode.FRIEND_REQUEST_NOT_FOUND, exception.getErrorCode());
    }
}