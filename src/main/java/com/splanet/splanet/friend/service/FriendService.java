package com.splanet.splanet.friend.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.friendRequest.entity.FriendRequest;
import com.splanet.splanet.friendRequest.repository.FriendRequestRepository;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.repository.PlanRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final PlanRepository planRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendService(FriendRepository friendRepository, PlanRepository planRepository, FriendRequestRepository friendRequestRepository) {
        this.friendRepository = friendRepository;
        this.planRepository = planRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    // 친구 목록 조회
    public List<FriendResponse> getFriends(Long userId) {
        List<Friend> friends = friendRepository.findByUserId(userId);

        // Friend 엔티티를 FriendResponse DTO로 변환
        return friends.stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();
                    return new FriendResponse(
                            friendUser.getId(),
                            friendUser.getNickname(),
                            friendUser.getProfileImage()
                    );
                })
                .collect(Collectors.toList());
    }

    // 친구의 공개 플랜 조회
    public ResponseEntity<List<PlanResponseDto>> getFriendPlan(Long friendId, Long userId) {
        // 친구 목록에 friendId가 있는지 확인
        boolean isFriend = friendRepository.existsByUserIdAndFriendId(userId, friendId);

        if (!isFriend) {
            throw new BusinessException(ErrorCode.FRIEND_NOT_FOUND);
        }

        List<Plan> publicPlans = planRepository.findAllByUserIdAndAccessibility(friendId, true);

        // 공개된 플랜이 없을 경우, 빈 목록 반환
        if (publicPlans.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<PlanResponseDto> planResponseDtos = publicPlans.stream()
                .map(plan -> PlanResponseDto.builder()
                        .id(plan.getId())
                        .title(plan.getTitle())
                        .description(plan.getDescription())
                        .startDate(plan.getStartDate())
                        .endDate(plan.getEndDate())
                        .accessibility(plan.getAccessibility())
                        .isCompleted(plan.getIsCompleted())
                        .createdAt(plan.getCreatedAt())
                        .updatedAt(plan.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(planResponseDtos);
    }

    // 친구 삭제(취소)하기
    @Transactional
    public ResponseEntity<Map<String, String>> unfriend(Long friendId, Long userId) {
        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new BusinessException(ErrorCode.FRIEND_NOT_FOUND);
        }

        friendRepository.deleteByRequesterIdAndReceiverId(userId, friendId);

        List<FriendRequest> pendingRequests = friendRequestRepository.findPendingRequestsByReceiverId(userId, friendId, FriendRequest.Status.PENDING);
        for (FriendRequest request : pendingRequests) {
            friendRequestRepository.delete(request);
        }

        return ResponseEntity.ok(Map.of("message", "친구 맺기 취소되었습니다!"));
    }
}