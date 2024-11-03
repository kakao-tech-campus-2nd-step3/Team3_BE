package com.splanet.splanet.plan.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder(toBuilder = true)
public class Plan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "유저는 필수입니다.")
    private User user;

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
    @Column(length = 100, nullable = false)
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 넘을 수 없습니다.")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Builder.Default
    @Column(nullable = true)
    private Boolean accessibility = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCompleted = false;


  public void validateDates() {
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
    }
  }
}
