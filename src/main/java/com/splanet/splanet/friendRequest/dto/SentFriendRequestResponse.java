package com.splanet.splanet.friendRequest.dto;

// 보낸 요청 정보담는 dto
public record SentFriendRequestResponse(
        Long id,
        Long receiverId,
        String receiverName,
        String status,
        String profileImage
) {}