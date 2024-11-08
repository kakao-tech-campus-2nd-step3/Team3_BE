package com.splanet.splanet.team.repository;

import com.splanet.splanet.team.entity.InvitationStatus;
import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.entity.TeamInvitation;
import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
  List<TeamInvitation> findAllByUserAndStatus(User user, InvitationStatus status);
  List<TeamInvitation> findAllByTeamAndStatus(Team team, InvitationStatus status);
  Optional<TeamInvitation> findByTeamAndUserAndStatus(Team team, User user, InvitationStatus status);
}