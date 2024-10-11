package com.splanet.splanet.friend.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.friend.repository.FriendRepository;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.entity.Plan;
import com.splanet.splanet.plan.repository.PlanRepository;
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
    private final PlanRepository planRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository, PlanRepository planRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
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
        List<Plan> publicPlans = planRepository.findAllByUserIdAndAccessibility(friendId, true);

        if (publicPlans.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAN_NOT_FOUND);
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
}