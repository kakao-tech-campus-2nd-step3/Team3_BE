package com.splanet.splanet.friendRequest.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.friend.entity.Friend;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
public class FriendRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public Friend accept() {
        return Friend.builder()
                .user(requester)
                .friend(receiver)
                .build();
    }
}