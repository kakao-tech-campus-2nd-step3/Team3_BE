package com.splanet.splanet.team;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
public class Team extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "teamname", nullable = false, length = 100)
  private String teamname;
}
