package com.splanet.splanet.team.controller;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @PostMapping
  public ResponseEntity<Team> createTeam(@RequestParam String teamName) {
    Team createdTeam = teamService.createTeam(teamName);
    return ResponseEntity.ok(createdTeam);
  }

  @GetMapping
  public ResponseEntity<List<Team>> getAllTeams() {
    List<Team> teams = teamService.getAllTeams();
    return ResponseEntity.ok(teams);
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<Team> getTeamById(@PathVariable Long teamId) {
    return teamService.getTeamById(teamId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{teamId}")
  public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @RequestParam String newTeamName) {
    Team updatedTeam = teamService.updateTeam(teamId, newTeamName);
    return ResponseEntity.ok(updatedTeam);
  }

  @DeleteMapping("/{teamId}")
  public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
    teamService.deleteTeam(teamId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Team> addUserToTeam(@PathVariable Long teamId, @PathVariable Long userId) {
    Team updatedTeam = teamService.addUserToTeam(teamId, userId);
    return ResponseEntity.ok(updatedTeam);
  }

  @DeleteMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Team> removeUserFromTeam(@PathVariable Long teamId, @PathVariable Long userId) {
    Team updatedTeam = teamService.removeUserFromTeam(teamId, userId);
    return ResponseEntity.ok(updatedTeam);
  }
}