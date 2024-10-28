package com.splanet.splanet.friendRequest.dto;

// 받은 요청 정보담는 dto
public record ReceivedFriendRequestResponse(
        Long id,
        Long requesterId,
        String requesterName,
        String status,
        String profileImage
) {}