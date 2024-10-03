package com.splanet.splanet.subscription.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.payment.entity.Payment;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
public class Subscription extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.ACTIVE;
        }
    }

    public enum Status {
        ACTIVE, CANCELED, EXPIRED
    }

    public enum Type {
        MONTHLY, YEARLY
    }

    public void cancel() {
        this.status = Status.CANCELED;
        this.endDate = LocalDateTime.now();
    }
}