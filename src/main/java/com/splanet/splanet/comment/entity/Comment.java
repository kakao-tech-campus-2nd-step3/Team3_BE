package com.splanet.splanet.comment.entity;

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
public class Comment extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
}