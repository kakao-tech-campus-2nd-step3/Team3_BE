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
          description = """
                    새로운 팀을 생성하며, 생성한 유저에게 팀 관리자 권한을 부여합니다.
                    
                    요청 매개변수:
                    - teamName (String): 팀 이름
                    - userId (Long): 팀을 생성하는 유저의 ID
                    
                    응답 예시:
                    ```
                    {
                      "id": 1,
                      "teamName": "My Team",
                      "createdAt": "2024-09-30T12:34:56",
                      "updatedAt": "2024-09-30T12:34:56"
                    }
                    ```
                    """
  )
  @PostMapping
  public ResponseEntity<Team> createTeam(@RequestParam String teamName, @RequestParam Long userId) {
    Team createdTeam = teamService.createTeam(teamName, userId);
    return ResponseEntity.ok(createdTeam);
  }

  @Operation(
          summary = "유저를 팀에 추가",
          description = """
                    특정 유저를 팀에 멤버로 추가합니다.
                    
                    요청 경로 매개변수:
                    - teamId (Long): 팀의 ID
                    - userId (Long): 추가할 유저의 ID
                    
                    응답 예시:
                    ```
                    {
                      "id": 1,
                      "teamName": "My Team",
                      "createdAt": "2024-09-30T12:34:56",
                      "updatedAt": "2024-09-30T12:34:56"
                    }
                    ```
                    """
  )
  @PostMapping("/{teamId}/users/{userId}")
  public ResponseEntity<Team> addUserToTeam(@PathVariable Long teamId, @PathVariable Long userId) {
    Team updatedTeam = teamService.addUserToTeam(teamId, userId);
    return ResponseEntity.ok(updatedTeam);
  }

  @Operation(
          summary = "유저를 팀 관리자 권한으로 승격",
          description = """
                    기존 팀 관리자가 다른 유저에게 팀 관리자 권한을 부여합니다.
                    
                    요청 경로 매개변수:
                    - teamId (Long): 팀의 ID
                    - userId (Long): 관리자 권한을 부여할 유저의 ID
                    - adminId (Long): 권한을 부여하는 현재 관리자의 ID
                    
                    응답: 성공 시 응답 본문 없음 (204 No Content)
                    """
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
          description = """
                    팀 관리자가 특정 닉네임을 가진 유저를 팀에 멤버로 추가합니다.
                    
                    요청 경로 및 매개변수:
                    - teamId (Long): 팀의 ID
                    - nickname (String): 추가할 유저의 닉네임
                    - adminId (Long): 유저를 추가하는 관리자의 ID
                    
                    응답 예시:
                    ```
                    {
                      "id": 1,
                      "teamName": "My Team",
                      "createdAt": "2024-09-30T12:34:56",
                      "updatedAt": "2024-09-30T12:34:56"
                    }
                    ```
                    """
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
          description = """
                    팀 관리자가 특정 유저를 팀에서 제거합니다.
                    
                    요청 경로 매개변수:
                    - teamId (Long): 팀의 ID
                    - userId (Long): 제거할 유저의 ID
                    - adminId (Long): 유저를 제거하는 관리자의 ID
                    
                    응답: 성공 시 응답 본문 없음 (204 No Content)
                    """
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
          description = """
                    팀에 속해 있는 모든 멤버를 조회합니다. 팀에 속한 유저는 누구나 이 기능을 사용할 수 있습니다.
                    
                    요청 경로 및 매개변수:
                    - teamId (Long): 팀의 ID
                    - userId (Long): 요청하는 유저의 ID
                    
                    응답 예시:
                    ```
                    [
                      {
                        "id": 1,
                        "nickname": "user1",
                        "profileImage": "profile1.png",
                        "isPremium": true,
                        "createdAt": "2024-09-30T12:34:56",
                        "updatedAt": "2024-09-30T12:34:56"
                      },
                      {
                        "id": 2,
                        "nickname": "user2",
                        "profileImage": "profile2.png",
                        "isPremium": false,
                        "createdAt": "2024-09-30T12:34:56",
                        "updatedAt": "2024-09-30T12:34:56"
                      }
                    ]
                    ```
                    """
  )
  @GetMapping("/{teamId}/members")
  public ResponseEntity<List<User>> getTeamMembers(
          @PathVariable Long teamId,
          @RequestParam Long userId) {
    List<User> members = teamService.getTeamMembers(teamId, userId);
    return ResponseEntity.ok(members);
  }
}