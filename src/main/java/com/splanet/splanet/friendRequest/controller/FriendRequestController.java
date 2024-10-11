package com.splanet.splanet.friendRequest.controller;

import com.splanet.splanet.friendRequest.dto.FriendRequestRequest;
import com.splanet.splanet.friendRequest.dto.FriendRequestResponse;
import com.splanet.splanet.friendRequest.service.FriendRequestService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendRequestController implements FriendRequestApi{

    private final FriendRequestService friendRequestService;
    private final UserRepository userRepository;

    public FriendRequestController(FriendRequestService friendRequestService, UserRepository userRepository) {
        this.friendRequestService = friendRequestService;
        this.userRepository = userRepository;
    }

    // 친구 요청 전송
    @Override
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@RequestBody FriendRequestRequest request) {
        Long requesterId = request.requesterId();
        Long receiverId = request.receiverId();

        friendRequestService.sendFriendRequest(requesterId, receiverId);

        FriendRequestResponse response = new FriendRequestResponse(
                null,
                requesterId,
                "친구 요청이 성공적으로 전송되었습니다.",
                "PENDING",
                "profileImageUrl"
        );

        return ResponseEntity.ok(response);
    }

    // 친구 요청 수락
    @Override
    public ResponseEntity<FriendRequestResponse> acceptFriendRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendRequestService.acceptFriendRequest(requestId));
    }

    // 친구 요청 거절
    @Override
    public ResponseEntity<FriendRequestResponse> rejectFriendRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendRequestService.rejectFriendRequest(requestId));
    }

    // 친구 요청 목록 조회(받은 요청)
    public ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(friendRequestService.getReceivedFriendRequests(userId));
    }

    // 친구 요청 목록 조회(보낸 요청)
    @Override
    public ResponseEntity<List<FriendRequestResponse>> getSentRequests(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(friendRequestService.getSentFriendRequests(userId));
    }
}