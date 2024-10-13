package com.splanet.splanet.friendRequest.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.ReceivedFriendRequestResponse;
import com.splanet.splanet.friendRequest.dto.SentFriendRequestResponse;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserRepository userRepository, FriendRepository friendRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    // 친구 요청 전송
    public void sendFriendRequest(Long userId, Long receiverId) {
        // 본인에게 요청 보낼 수 없음
        if (userId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.SELF_FRIEND_REQUEST_NOT_ALLOWED);
        }

        // 요청자가 이미 친구 목록에 있는지 확인
        if (friendRepository.existsByUserIdAndFriendId(userId, receiverId)) {
            throw new BusinessException(ErrorCode.FRIEND_ALREADY_EXISTS);
        }

        // 이미 보낸 요청이 있는지 확인
        List<FriendRequest> existingRequests = friendRequestRepository.findPendingRequestsByReceiverId(receiverId, userId, FriendRequest.Status.PENDING);
        if (!existingRequests.isEmpty()) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequest.Status.PENDING)
                .build();

        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 수락
    public ReceivedFriendRequestResponse acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendRequest.getStatus() != FriendRequest.Status.PENDING) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED_OR_REJECTED);
        }

        if (!friendRequest.getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_RECEIVER);
        }

        FriendRequest updatedFriendRequest = FriendRequest.builder()
                .id(friendRequest.getId())
                .requester(friendRequest.getRequester())
                .receiver(friendRequest.getReceiver())
                .status(FriendRequest.Status.ACCEPTED)
                .createdAt(friendRequest.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        friendRequestRepository.save(updatedFriendRequest);

        User requester = friendRequest.getRequester();
        User receiver = friendRequest.getReceiver();

        Friend friend1 = Friend.builder()
                .user(requester)
                .friend(receiver)
                .build();

        Friend friend2 = Friend.builder()
                .user(receiver)
                .friend(requester)
                .build();

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        return new ReceivedFriendRequestResponse(
                friendRequest.getId(),
                requester.getId(),
                requester.getNickname(),
                friendRequest.getStatus().name(),
                requester.getProfileImage()
        );
    }

    // 친구 요청 거절
    public ReceivedFriendRequestResponse rejectFriendRequest(Long requestId, Long userId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendRequest.getStatus() != FriendRequest.Status.PENDING) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_ACCEPTED_OR_REJECTED);
        }

        if (friendRequest.getRequester().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_RECEIVER);
        }

        FriendRequest updatedFriendRequest = FriendRequest.builder()
                .id(friendRequest.getId())
                .requester(friendRequest.getRequester())
                .receiver(friendRequest.getReceiver())
                .status(FriendRequest.Status.REJECTED)
                .createdAt(friendRequest.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        friendRequestRepository.save(updatedFriendRequest);

        User requester = friendRequest.getRequester();

        return new ReceivedFriendRequestResponse(
                friendRequest.getId(),
                requester.getId(),
                requester.getNickname(),
                updatedFriendRequest.getStatus().name(),
                requester.getProfileImage()
        );
    }

    // 친구 요청 목록 조회(받은 요청)
    public List<ReceivedFriendRequestResponse> getReceivedFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverId(userId);

        // PENDING인 요청만
        return requests.stream()
                .filter(request -> request.getStatus() == FriendRequest.Status.PENDING)
                .map(request -> new ReceivedFriendRequestResponse(
                        request.getId(),
                        request.getRequester().getId(),
                        request.getRequester().getNickname(),
                        request.getStatus().name(),
                        request.getRequester().getProfileImage()
                ))
                .collect(Collectors.toList());
    }

    // 친구 요청 목록 조회(보낸 요청)
    public List<SentFriendRequestResponse> getSentFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByRequesterId(userId);

        // PENDING인 요청만
        return requests.stream()
                .filter(request -> request.getStatus() == FriendRequest.Status.PENDING)
                .map(request -> new SentFriendRequestResponse(
                        request.getId(),
                        request.getReceiver().getId(),
                        request.getReceiver().getNickname(),
                        request.getStatus().name(),
                        request.getRequester().getProfileImage()
                ))
                .collect(Collectors.toList());
    }
}