package com.splanet.splanet.friendRequest.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.dto.FriendRequestResponse;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;

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
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FriendRequest friendRequest = new FriendRequest(requester, receiver, FriendRequest.Status.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 수락
    public FriendRequestResponse acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        User requester = friendRequest.getRequester();
        User receiver = friendRequest.getReceiver();

        Friend friend1 = new Friend(requester, receiver);  // 요청한 사람 -> 수락한 사람
        Friend friend2 = new Friend(receiver, requester);  // 수락한 사람 -> 요청한 사람

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        return new FriendRequestResponse(
                friendRequest.getId(),
                requester.getId(),
                requester.getNickname(),
                friendRequest.getStatus().name(),
                requester.getProfileImage()
        );
    }

    // 친구 요청 거절
    public FriendRequestResponse rejectFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        friendRequest.setStatus(FriendRequest.Status.REJECTED);
        friendRequestRepository.save(friendRequest);

        User requester = friendRequest.getRequester();
        User receiver = friendRequest.getReceiver();

        return new FriendRequestResponse(
                friendRequest.getId(),
                requester.getId(),
                requester.getNickname(),
                friendRequest.getStatus().name(),
                requester.getProfileImage()
        );
    }

    // 친구 요청 목록 조회(받은 요청)
    public List<FriendRequestResponse> getReceivedFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverId(userId);

        // PENDING인 요청만
        return requests.stream()
                .filter(request -> request.getStatus() == FriendRequest.Status.PENDING)
                .map(request -> new FriendRequestResponse(
                        request.getId(),
                        request.getRequester().getId(),
                        request.getRequester().getNickname(),
                        request.getStatus().name(),
                        request.getRequester().getProfileImage()
                ))
                .collect(Collectors.toList());
    }

    // 친구 요청 목록 조회(보낸 요청)
    public List<FriendRequestResponse> getSentFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByRequesterId(userId);

        // PENDING인 요청만
        return requests.stream()
                .filter(request -> request.getStatus() == FriendRequest.Status.PENDING)
                .map(request -> new FriendRequestResponse(
                        request.getId(),
                        request.getReceiver().getId(),
                        request.getReceiver().getNickname(),
                        request.getStatus().name(),
                        request.getRequester().getProfileImage()
                ))
                .collect(Collectors.toList());
    }
}