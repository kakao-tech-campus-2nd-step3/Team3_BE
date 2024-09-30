package com.splanet.splanet.team.entity;

import com.splanet.splanet.core.BaseEntity;
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

  @Size(max = 100)
  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TeamUserRelation> teamUserRelations;
}