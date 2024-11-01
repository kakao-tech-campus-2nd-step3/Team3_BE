package com.splanet.splanet.friendRequest.controller;

import com.splanet.splanet.friendRequest.dto.FriendRequestCreateRequest;
import com.splanet.splanet.friendRequest.dto.ReceivedFriendRequestResponse;
import com.splanet.splanet.friendRequest.dto.SentFriendRequestResponse;
import com.splanet.splanet.friendRequest.dto.SuccessResponse;
import com.splanet.splanet.friendRequest.service.FriendRequestService;
import com.splanet.splanet.user.repository.UserRepository;
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
    public ResponseEntity<SuccessResponse> sendFriendRequest(@AuthenticationPrincipal Long userId,
                                                             @RequestBody FriendRequestCreateRequest request) {
        Long receiverId = request.receiverId();
        friendRequestService.sendFriendRequest(userId, receiverId);
        SuccessResponse response = new SuccessResponse("친구 요청이 성공적으로 전송되었습니다.");

        return ResponseEntity.ok(response);
    }

    // 친구 요청 수락
    @Override
    public ResponseEntity<ReceivedFriendRequestResponse> acceptFriendRequest(@AuthenticationPrincipal Long userId,
                                                                             @PathVariable Long requestId) {
        ReceivedFriendRequestResponse response = friendRequestService.acceptFriendRequest(requestId, userId);
        return ResponseEntity.ok(response);
    }

    // 친구 요청 거절
    @Override
    public ResponseEntity<ReceivedFriendRequestResponse> rejectFriendRequest(@PathVariable Long requestId, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(friendRequestService.rejectFriendRequest(requestId, userId));
    }

    // 친구 요청 목록 조회(받은 요청)
    public ResponseEntity<List<ReceivedFriendRequestResponse>> getReceivedRequests(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(friendRequestService.getReceivedFriendRequests(userId));
    }

    // 친구 요청 목록 조회(보낸 요청)
    @Override
    public ResponseEntity<List<SentFriendRequestResponse>> getSentRequests(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(friendRequestService.getSentFriendRequests(userId));
    }

    // 친구 요청 취소
    @Override
    public ResponseEntity<SuccessResponse> cancelFriendRequest(@PathVariable Long requestId,
                                                               @AuthenticationPrincipal Long userId) {
        friendRequestService.cancelFriendRequest(requestId, userId);
        SuccessResponse response = new SuccessResponse("친구 요청이 성공적으로 취소되었습니다.");
        return ResponseEntity.ok(response);
    }
}