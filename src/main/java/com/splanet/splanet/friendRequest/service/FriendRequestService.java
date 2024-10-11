package com.splanet.splanet.friendRequest.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
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
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    // 친구 요청 전송
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // FriendRequest 객체를 생성할 때 User 객체를 사용
        FriendRequest friendRequest = new FriendRequest(requester, receiver, FriendRequest.Status.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 수락
    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        // 요청 상태를 ACCEPTED로 변경
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 거절
    public void rejectFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        // 요청 상태를 REJECTED로 변경
        friendRequest.setStatus(FriendRequest.Status.REJECTED);
        friendRequestRepository.save(friendRequest);
    }

    // 친구 요청 목록 조회(받은 요청)
    public List<FriendRequestResponse> getReceivedFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverId(userId);
        return requests.stream()
                .map(request -> new FriendRequestResponse(
                        "이 유저로부터 요청이 왔습니다.: " + request.getRequester().getId()
                ))
                .collect(Collectors.toList());
    }

    // 친구 요청 목록 조회(보낸 요청)
    public List<FriendRequestResponse> getSentFriendRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(request -> new FriendRequestResponse(
                        "이 유저에게 요청을 보냈습니다.: " + request.getReceiver().getId()
                ))
                .collect(Collectors.toList());
    }
}