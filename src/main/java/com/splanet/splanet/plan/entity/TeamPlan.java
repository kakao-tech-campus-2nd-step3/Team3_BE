package com.splanet.splanet.plan.entity;

import com.splanet.splanet.core.BaseEntity;
import com.splanet.splanet.team.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
public class TeamPlan extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @NotBlank
  @Size(max = 100)
  @Column(length = 100, nullable = false)
  private String title;

  @Size(max = 1000)
  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  @Column(nullable = true)
  private Boolean accessibility = true;

  @Column(nullable = false)
  private Boolean isCompleted = false;

  public void updatePlan(String title, String description, LocalDateTime startDate, LocalDateTime endDate, Boolean accessibility, Boolean isCompleted) {
    this.title = title;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.accessibility = accessibility;
    this.isCompleted = isCompleted;

  }
}
