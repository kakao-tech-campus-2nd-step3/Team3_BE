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

@RestController
public class FriendController implements FriendApi {

    private final FriendService friendService;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    public FriendController(FriendService friendService, UserRepository userRepository, PlanRepository planRepository) {
        this.friendService = friendService;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
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
}
