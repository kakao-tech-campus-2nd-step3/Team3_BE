package com.splanet.splanet.team.controller;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.service.TeamService;
import com.splanet.splanet.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teams")
@Tag(name = "Team API", description = "팀 관련 CRUD 및 관리자 권한 부여 API")
public class TeamController {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @Operation(
          summary = "팀 생성",
          description = "새로운 팀을 생성하며, 생성한 유저에게 팀 관리자 권한을 부여합니다."
  )
  @PostMapping
  public ResponseEntity<Team> createTeam(@RequestParam String teamName, @RequestParam Long userId) {
    Team createdTeam = teamService.createTeam(teamName, userId);
    return ResponseEntity.ok(createdTeam);
  }

  @Operation(
          summary = "유저를 팀에 추가",
          description = "특정 유저를 팀에 멤버로 추가합니다."
  )
  @PostMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Team> addUserToTeam(@PathVariable Long teamId, @PathVariable Long userId) {
    Team updatedTeam = teamService.addUserToTeam(teamId, userId);
    return ResponseEntity.ok(updatedTeam);
  }

  @Operation(
          summary = "유저를 팀 관리자 권한으로 승격",
          description = "기존 팀 관리자가 다른 유저에게 팀 관리자 권한을 부여합니다."
  )
  @PutMapping("/{teamId}/users/{userId}/promote")
  public ResponseEntity<Void> promoteUserToAdmin(
          @PathVariable Long teamId,
          @PathVariable Long userId,
          @RequestParam Long adminId) {
    teamService.promoteUserToAdmin(teamId, userId, adminId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
          summary = "유저 닉네임을 검색해 팀에 추가",
          description = "팀 관리자가 특정 닉네임을 가진 유저를 팀에 멤버로 추가합니다."
  )
  @PostMapping("/{teamId}/users/add")
  public ResponseEntity<Team> addUserToTeamByNickname(
          @PathVariable Long teamId,
          @RequestParam String nickname,
          @RequestParam Long adminId) {
    Team updatedTeam = teamService.addUserToTeamByNickname(teamId, nickname, adminId);
    return ResponseEntity.ok(updatedTeam);
  }

  @Operation(
          summary = "유저를 팀에서 제거",
          description = "팀 관리자가 특정 유저를 팀에서 제거합니다."
  )
  @DeleteMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Void> removeUserFromTeam(
          @PathVariable Long teamId,
          @PathVariable Long userId,
          @RequestParam Long adminId) {
    teamService.removeUserFromTeam(teamId, userId, adminId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
          summary = "팀 멤버 조회",
          description = "팀에 속해 있는 모든 멤버를 조회합니다. 팀에 속한 유저는 누구나 이 기능을 사용할 수 있습니다."
  )
  @GetMapping("/{teamId}/members")
  public ResponseEntity<List<User>> getTeamMembers(
          @PathVariable Long teamId,
          @RequestParam Long userId) {
    List<User> members = teamService.getTeamMembers(teamId, userId);
    return ResponseEntity.ok(members);
  }

}