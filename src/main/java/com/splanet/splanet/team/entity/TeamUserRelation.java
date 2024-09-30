package com.splanet.splanet.team.entity;

import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TeamUserRelation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserTeamRole role;

  public TeamUserRelation(Team team, User user, UserTeamRole role) {
    this.team = team;
    this.user = user;
    this.role = role;
  }

  public void promoteToAdmin() {
    this.role = UserTeamRole.ADMIN;
  }
}