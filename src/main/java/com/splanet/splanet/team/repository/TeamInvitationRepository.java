package com.splanet.splanet.team.repository;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamInvitation;
import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
  Optional<TeamInvitation> findByTeamAndUser(Team team, User user);
}