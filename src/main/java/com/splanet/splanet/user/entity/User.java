package com.splanet.splanet.user;

import com.splanet.splanet.core.BaseEntity;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class User extends BaseEntity {

  @Column(name = "nickname", nullable = false, length = 100, unique = true)
  private String nickname;

  @Column(name = "profile_image", length = 255)
  private String profileImage;

  @Column(name = "is_premium")
  private Boolean isPremium;

}
