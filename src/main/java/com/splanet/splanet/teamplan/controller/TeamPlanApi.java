package com.splanet.splanet.teamplan.controller;

import com.splanet.splanet.teamplan.dto.TeamPlanRequestDto;
import com.splanet.splanet.teamplan.dto.TeamPlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Tag(name = "Team Plan API", description = "팀 플랜 관련 CRUD API")
@RequestMapping("/api/teams/{teamId}/plans")
public interface TeamPlanApi {

  // 팀 플랜 생성 API
  @Operation(summary = "팀 플랜 생성", description = "팀 관리자가 팀 플랜을 생성합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 플랜이 성공적으로 생성되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없습니다.", content = @Content)
  })
  @PostMapping
  ResponseEntity<TeamPlanResponseDto> createTeamPlan(
          @AuthenticationPrincipal Long userId,
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @RequestBody TeamPlanRequestDto requestDto);

  // 특정 팀 플랜 조회 API
  @Operation(summary = "팀 플랜 조회", description = "팀에 속한 사용자가 특정 팀 플랜을 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 플랜 조회 성공"),
          @ApiResponse(responseCode = "404", description = "팀 플랜을 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping("/{planId}")
  ResponseEntity<TeamPlanResponseDto> getTeamPlan(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "플랜의 ID", example = "1") @PathVariable Long planId);

  // 모든 팀 플랜 조회 API
  @Operation(summary = "팀의 모든 플랜 조회", description = "팀에 속한 사용자가 팀의 모든 플랜을 조회합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 플랜 조회 성공"),
          @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없습니다.", content = @Content)
  })
  @GetMapping
  ResponseEntity<List<TeamPlanResponseDto>> getAllTeamPlans(
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId);

  // 팀 플랜 수정 API
  @Operation(summary = "팀 플랜 수정", description = "팀 관리자가 팀 플랜을 수정합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "팀 플랜이 성공적으로 수정되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀 플랜을 찾을 수 없습니다.", content = @Content)
  })
  @PutMapping("/{planId}")
  ResponseEntity<TeamPlanResponseDto> updateTeamPlan(
          @AuthenticationPrincipal Long userId,
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "플랜의 ID", example = "1") @PathVariable Long planId,
          @RequestBody TeamPlanRequestDto requestDto);

  // 팀 플랜 삭제 API
  @Operation(summary = "팀 플랜 삭제", description = "팀 관리자가 팀 플랜을 삭제합니다.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "팀 플랜이 성공적으로 삭제되었습니다."),
          @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
          @ApiResponse(responseCode = "404", description = "팀 플랜을 찾을 수 없습니다.", content = @Content)
  })
  @DeleteMapping("/{planId}")
  ResponseEntity<Void> deleteTeamPlan(
          @AuthenticationPrincipal Long userId,
          @Parameter(description = "팀의 ID", example = "1") @PathVariable Long teamId,
          @Parameter(description = "플랜의 ID", example = "1") @PathVariable Long planId);
}