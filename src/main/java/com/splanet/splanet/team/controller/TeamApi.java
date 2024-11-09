package com.splanet.splanet.team.controller;

import com.splanet.splanet.team.dto.TeamDto;
import com.splanet.splanet.team.dto.TeamInvitationDto;
import com.splanet.splanet.team.dto.TeamMemberDto;
import com.splanet.splanet.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Team API", description = "팀 관련 CRUD 및 관리자 권한 부여 API")
@RequestMapping("/api/teams")
public interface TeamApi {

  @Operation(summary = "팀 생성", description = "새로운 팀을 생성하며, 생성한 유저에게 팀 관리자 권한을 부여합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀이 성공적으로 생성되었습니다."),
          @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content)
  })
  @PostMapping
  ResponseEntity<TeamDto> createTeam(
          @Parameter(description = "팀 이름", example = "My Team") @RequestParam String teamName,
          @Parameter(description = "팀을 생성하는 유저의 ID", example = "1") @AuthenticationPrincipal Long userId);

  @Operation(summary = "유저 초대", description = "팀 관리자가 닉네임으로 특정 유저를 팀에 초대합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "유저가 성공적으로 초대되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.", content = @Content),
          @ApiResponse(responseCode = "400", description = "해당 유저는 이미 팀에 속해 있습니다.", content = @Content)
  })
  @PostMapping("/{teamId}/invite")
  ResponseEntity<TeamInvitationDto> inviteUserToTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "초대하는 관리자의 ID", example = "3") @AuthenticationPrincipal Long adminId,
          @Parameter(description = "초대할 유저의 닉네임", example = "nickname123") @RequestParam String nickname);

  @Operation(summary = "초대 수락 또는 거절", description = "유저가 팀 초대를 수락하거나 거절합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "초대가 성공적으로 처리되었습니다."),
          @ApiResponse(responseCode = "404", description = "초대를 찾을 수 없습니다."),
          @ApiResponse(responseCode = "400", description = "초대가 이미 처리되었습니다.")
  })
  @PutMapping("/invitation/{invitationId}/response")
  ResponseEntity<Void> handleInvitationResponse(
          @Parameter(description = "초대 ID", example = "1") @PathVariable Long invitationId,
          @Parameter(description = "초대 수락 여부", example = "true") @RequestParam boolean isAccepted,
          @AuthenticationPrincipal Long userId);

  @Operation(summary = "사용자 초대 목록 조회", description = "사용자가 자신의 초대 목록을 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "초대 목록 조회 성공"),
          @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping("/invitations")
  ResponseEntity<List<TeamInvitationDto>> getUserPendingInvitations(@AuthenticationPrincipal Long userId);

  @Operation(summary = "팀 멤버 조회", description = "특정 팀의 모든 멤버를 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 멤버 조회 성공"),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping("/{teamId}/members")
  ResponseEntity<List<TeamMemberDto>> getTeamMembers(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "조회하는 유저의 ID", example = "1") @AuthenticationPrincipal Long userId);

  @Operation(summary = "유저 권한 수정", description = "관리자가 유저의 권한을 수정합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "유저의 권한이 성공적으로 수정되었습니다.",
                  content = @Content(mediaType = "*/*",
                          examples = @ExampleObject(value = "{ \"userId\": 2, \"role\": \"ADMIN\" }"))),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "유저 또는 팀을 찾을 수 없습니다.", content = @Content)
  })
  @PutMapping("/{teamId}/users/{userId}/role")
  ResponseEntity<Map<String, Object>> updateUserRole(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "유저의 ID", example = "2") @PathVariable Long userId,
          @AuthenticationPrincipal Long adminId);

  @Operation(summary = "팀 관리자가 보낸 초대 목록 조회", description = "팀 관리자가 보낸 초대 중 대기 상태인 초대를 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "초대 목록 조회 성공"),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping("/{teamId}/invitations")
  ResponseEntity<List<TeamInvitationDto>> getAdminPendingInvitations(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @AuthenticationPrincipal Long adminId);

  @Operation(summary = "팀 관리자가 초대 취소", description = "팀 관리자가 보낸 초대를 취소합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "초대가 성공적으로 취소되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "초대를 찾을 수 없습니다.", content = @Content)
  })
  @DeleteMapping("/invitation/{invitationId}/cancel")
  ResponseEntity<Void> cancelTeamInvitation(
          @Parameter(description = "초대 ID", example = "1") @PathVariable Long invitationId,
          @AuthenticationPrincipal Long adminId);

  @Operation(summary = "팀에서 유저 내보내기", description = "관리자가 특정 유저를 팀에서 내보냅니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "유저가 성공적으로 팀에서 내보내졌습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
          @ApiResponse(responseCode = "404", description = "유저 또는 팀을 찾을 수 없습니다.")
  })
  @DeleteMapping("/{teamId}/users/{userId}")
  ResponseEntity<Void> kickUserFromTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "내보낼 유저의 ID", example = "2") @PathVariable Long userId,
          @AuthenticationPrincipal Long adminId);
  @Operation(summary = "사용자가 속한 팀 목록 조회", description = "사용자가 속한 팀 목록을 조회합니다.")

  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 목록 조회 성공"),
          @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping("/my-teams")
  ResponseEntity<List<TeamDto>> getUserTeams(@AuthenticationPrincipal Long userId);

  @Operation(summary = "팀 삭제", description = "팀 관리자가 팀을 삭제합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "팀이 성공적으로 삭제되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없습니다.", content = @Content)
  })
  @DeleteMapping("/{teamId}")
  ResponseEntity<Void> deleteTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @AuthenticationPrincipal Long adminId);

  // 일반 사용자가 팀을 나가는 기능 추가
  @Operation(summary = "팀 나가기", description = "일반 사용자가 팀에서 나가는 기능입니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "팀에서 성공적으로 나갔습니다."),
          @ApiResponse(responseCode = "404", description = "팀 또는 유저를 찾을 수 없습니다.", content = @Content)
  })
  @DeleteMapping("/{teamId}/leave")
  ResponseEntity<Void> leaveTeam(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @AuthenticationPrincipal Long userId);
}