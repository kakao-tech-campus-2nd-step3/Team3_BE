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

  // 관리자 권한으로 승격
  public void promoteToAdmin() {
    this.role = UserTeamRole.ADMIN;
  }

  // 일반 유저로 강등
  public void demoteToMember() {
    this.role = UserTeamRole.MEMBER;
  }
}