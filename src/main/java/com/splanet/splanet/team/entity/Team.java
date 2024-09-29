package com.splanet.splanet.team.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@SuperBuilder
public class Team extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Size(max = 100)
  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;

  @ManyToMany
  @JoinTable(
          name = "team_user",
          joinColumns = @JoinColumn(name = "team_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> users;
}