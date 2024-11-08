package com.splanet.splanet.friend.controller;

import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.service.FriendService;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FriendController implements FriendApi {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @Override
    public ResponseEntity<List<FriendResponse>> getFriends(Long userId) {
        List<FriendResponse> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @Override
    public ResponseEntity<List<PlanResponseDto>> getFriendPlan(
            @PathVariable Long friendId,
            @AuthenticationPrincipal Long userId) {
        return friendService.getFriendPlan(friendId, userId);
    }

    @Override
    public ResponseEntity<Map<String, String>> unfriend(
            @PathVariable Long friendId,
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Map<String, String>> responseEntity = friendService.unfriend(friendId, userId);
        return responseEntity;
    }
}