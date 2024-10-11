package com.splanet.splanet.team.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.teamplan.entity.TeamPlan;
import com.splanet.splanet.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

  @NotBlank(message = "팀 이름은 필수 입력 항목입니다.")
  @Size(max = 100)
  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TeamUserRelation> teamUserRelations;

  @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
  private List<TeamUserRelation> userRelations;

  @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
  private List<TeamPlan> teamPlans;


  public Team(String teamName, User user) {
    this.teamName = teamName;
    this.user = user;
  }
}