package com.splanet.splanet.team.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Entity
public class Team extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Size(max = 100) // 최대 100자 제한
  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;
}
