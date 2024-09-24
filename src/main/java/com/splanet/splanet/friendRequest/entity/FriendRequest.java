package com.splanet.splanet.friendRequest.entity;

import com.splanet.splanet.core.BaseEntity;
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

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

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
}