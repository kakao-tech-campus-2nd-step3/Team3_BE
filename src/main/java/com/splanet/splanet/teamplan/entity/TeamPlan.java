package com.splanet.splanet.teamplan.entity;

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
@SuperBuilder(toBuilder = true)
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

  public TeamPlan updatePlan(String title, String description, LocalDateTime startDate, LocalDateTime endDate, Boolean accessibility, Boolean isCompleted) {
    return this.toBuilder()
            .title(title)
            .description(description)
            .startDate(startDate)
            .endDate(endDate)
            .accessibility(accessibility)
            .isCompleted(isCompleted)
            .build();
  }

}
