package com.splanet.splanet.user.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.subscription.entity.Subscription;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class User extends BaseEntity {

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Subscription> subscriptions = new ArrayList<>();

  @NotBlank
  @Size(max = 100)
  @Column(name = "nickname", nullable = false, length = 100, unique = true)
  private String nickname;

  @Size(max = 2083)
  @Column(name = "profile_image", length = 2083)
  private String profileImage;

  @Column(name = "kakao_id", unique = true)
  private Long kakaoId;

  @Builder.Default
  @Column(name = "is_premium")
  private Boolean isPremium = false;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<FcmToken> fcmTokens = new ArrayList<>();

}