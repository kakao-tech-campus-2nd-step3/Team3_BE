package com.splanet.splanet.notification.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class FcmToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    private String deviceType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isNotificationEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer notificationOffset = 10;

    public LocalDateTime calculateNotificationTime(LocalDateTime planStartDate) {
        return planStartDate.minusMinutes(notificationOffset);
    }
}
