package com.splanet.splanet.team.repository;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamUserRelation;
import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamUserRelationRepository extends JpaRepository<TeamUserRelation, Long> {

  List<TeamUserRelation> findAllByTeam(Team team);

  Optional<TeamUserRelation> findByTeamAndUser(Team team, User user);
}