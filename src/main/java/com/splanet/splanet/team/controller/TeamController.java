package com.splanet.splanet.team.controller;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.service.TeamService;
import com.splanet.splanet.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "팀 생성", description = "새로운 팀을 생성하며, 생성한 유저에게 팀 관리자 권한을 부여합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀이 성공적으로 생성되었습니다."),
          @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
  })
  @PostMapping
  public ResponseEntity<Team> createTeam(
          @Parameter(description = "팀 이름", example = "My Team") @RequestParam String teamName,
          @Parameter(description = "팀을 생성하는 유저의 ID", example = "1") @RequestParam Long userId) {
    Team createdTeam = teamService.createTeam(teamName, userId);
    return ResponseEntity.ok(createdTeam);
  }

  @Operation(summary = "유저를 팀에 추가", description = "특정 유저를 팀에 멤버로 추가합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "유저가 성공적으로 팀에 추가되었습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.")
  })
  @PostMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Team> addUserToTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "추가할 유저의 ID", example = "2") @PathVariable Long userId) {
    Team updatedTeam = teamService.addUserToTeam(teamId, userId);
    return ResponseEntity.ok(updatedTeam);
  }

  @Operation(summary = "유저를 팀 관리자 권한으로 승격", description = "기존 팀 관리자가 다른 유저에게 팀 관리자 권한을 부여합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "유저가 성공적으로 팀 관리자 권한으로 승격되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.")
  })
  @PutMapping("/{teamId}/users/{userId}/promote")
  public ResponseEntity<Void> promoteUserToAdmin(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "관리자 권한을 부여할 유저의 ID", example = "2") @PathVariable Long userId,
          @Parameter(description = "권한을 부여하는 현재 관리자의 ID", example = "3") @RequestParam Long adminId) {
    teamService.promoteUserToAdmin(teamId, userId, adminId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "유저 닉네임을 검색해 팀에 추가", description = "팀 관리자가 특정 닉네임을 가진 유저를 팀에 멤버로 추가합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "유저가 성공적으로 팀에 추가되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.")
  })
  @PostMapping("/{teamId}/users/add")
  public ResponseEntity<Team> addUserToTeamByNickname(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "추가할 유저의 닉네임", example = "nickname123") @RequestParam String nickname,
          @Parameter(description = "유저를 추가하는 관리자의 ID", example = "3") @RequestParam Long adminId) {
    Team updatedTeam = teamService.addUserToTeamByNickname(teamId, nickname, adminId);
    return ResponseEntity.ok(updatedTeam);
  }

  @Operation(summary = "유저를 팀에서 제거", description = "팀 관리자가 특정 유저를 팀에서 제거합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "유저가 성공적으로 팀에서 제거되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.")
  })
  @DeleteMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Void> removeUserFromTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "제거할 유저의 ID", example = "2") @PathVariable Long userId,
          @Parameter(description = "유저를 제거하는 관리자의 ID", example = "3") @RequestParam Long adminId) {
    teamService.removeUserFromTeam(teamId, userId, adminId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "팀 멤버 조회", description = "팀에 속해 있는 모든 멤버를 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 멤버가 성공적으로 조회되었습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.")
  })
  @GetMapping("/{teamId}/members")
  public ResponseEntity<List<User>> getTeamMembers(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "요청하는 유저의 ID", example = "2") @RequestParam Long userId) {
    List<User> members = teamService.getTeamMembers(teamId, userId);
    return ResponseEntity.ok(members);
  }
}