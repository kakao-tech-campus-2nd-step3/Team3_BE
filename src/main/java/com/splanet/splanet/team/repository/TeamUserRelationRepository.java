package com.splanet.splanet.team.repository;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamUserRelationRepository extends JpaRepository<TeamUserRelation, Long> {

  List<TeamUserRelation> findAllByTeam(Team team);

  Optional<TeamUserRelation> findByTeamAndUser(Team team, User user);

  @EntityGraph(attributePaths = "user")
  @Query("SELECT t FROM TeamUserRelation t JOIN FETCH t.user WHERE t.team.id = :teamId")
  List<TeamUserRelation> findAllByTeamWithUser(@Param("teamId") Long teamId);

  @Query("SELECT t FROM TeamUserRelation t WHERE t.team.id = :teamId AND t.user.id = :userId")
  Optional<TeamUserRelation> findByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

}