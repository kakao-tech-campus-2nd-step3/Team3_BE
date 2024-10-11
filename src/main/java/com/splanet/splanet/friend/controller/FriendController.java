package com.splanet.splanet.friend.controller;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.service.FriendService;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController implements FriendApi {

    private final FriendService friendService;
    private final UserRepository userRepository;

    public FriendController(FriendService friendService, UserRepository userRepository) {
        this.friendService = friendService;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<List<FriendResponse>> getFriends(Long userId) {
        List<FriendResponse> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @Override
    public ResponseEntity<String> getFriendPlan(Long friendId, Long userId) {
        // 친구의 닉네임을 반환하는 임시 구현
        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(friendUser.getNickname());
    }
}
