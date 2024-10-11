package com.splanet.splanet.friendRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequestResponse {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private String status;
    private String profileImage;
}