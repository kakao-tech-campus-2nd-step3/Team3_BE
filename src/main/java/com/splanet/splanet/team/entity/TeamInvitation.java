package com.splanet.splanet.team.entity;

import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TeamInvitation {

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
  private InvitationStatus status;

  public TeamInvitation(Team team, User user) {
    this.team = team;
    this.user = user;
    this.status = InvitationStatus.PENDING;
  }

  public void accept() {
    this.status = InvitationStatus.ACCEPTED;
  }
  public void reject() {
    this.status = InvitationStatus.REJECTED;
  }
}

