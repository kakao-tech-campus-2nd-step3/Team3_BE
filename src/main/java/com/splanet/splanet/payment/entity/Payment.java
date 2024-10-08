package com.splanet.splanet.payment.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.subscription.entity.Subscription;
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
public class Payment extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "subscription_id", nullable = false, unique = true)
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "price")
    private int price;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }

    public enum Status {
        COMPLETED,
        PENDING,
        FAILED
    }
}