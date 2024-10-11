package com.splanet.splanet.friendRequest.controller;

import com.splanet.splanet.friendRequest.dto.FriendRequestRequest;
import com.splanet.splanet.friendRequest.dto.FriendRequestResponse;
import com.splanet.splanet.friendRequest.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendRequestController implements FriendRequestApi{

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    // 친구 요청 전송
    @Override
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@RequestBody FriendRequestRequest request) {
        friendRequestService.sendFriendRequest(request.requesterId(), request.receiverId());
        return ResponseEntity.ok(new FriendRequestResponse("친구 요청이 성공적으로 전송되었습니다."));
    }

    // 친구 요청 수락
    @Override
    public ResponseEntity<FriendRequestResponse> acceptFriendRequest(@PathVariable Long requestId) {
        friendRequestService.acceptFriendRequest(requestId);
        return ResponseEntity.ok(new FriendRequestResponse("친구 요청이 성공적으로 수락되었습니다."));
    }

    // 친구 요청 거절
    @Override
    public ResponseEntity<FriendRequestResponse> rejectFriendRequest(@PathVariable Long requestId) {
        friendRequestService.rejectFriendRequest(requestId);
        return ResponseEntity.ok(new FriendRequestResponse("친구 요청이 성공적으로 거절되었습니다."));
    }

    // 친구 요청 목록 조회(받은 요청)
    @Override
    public ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(@RequestParam Long userId) {
        List<FriendRequestResponse> requests = friendRequestService.getReceivedFriendRequests(userId);
        return ResponseEntity.ok(requests);
    }

    // 친구 요청 목록 조회(보낸 요청)
    @Override
    public ResponseEntity<List<FriendRequestResponse>> getSentRequests(@RequestParam Long userId) {
        List<FriendRequestResponse> requests = friendRequestService.getSentFriendRequests(userId);
        return ResponseEntity.ok(requests);
    }
}