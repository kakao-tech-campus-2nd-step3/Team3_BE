package com.splanet.splanet.friend.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    // 친구 목록 조회
    public List<FriendResponse> getFriends(Long userId) {
        List<Friend> friends = friendRepository.findByUserId(userId);

        // Friend 엔티티를 FriendResponse DTO로 변환
        return friends.stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();
                    return new FriendResponse(
                            friendUser.getKakaoId(),
                            friendUser.getNickname(),
                            friendUser.getProfileImage()
                    );
                })
                .collect(Collectors.toList());
    }

    // 친구의 플랜 조회 (plan api 머지 전이라 일단 친구 닉네임 반환으로 임시설정)
    public ResponseEntity<String> getFriendPlan(Long friendId, Long userId) {
        Friend friendRelationship = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User friend = friendRelationship.getFriend();

        return ResponseEntity.ok(friend.getNickname());
    }
}