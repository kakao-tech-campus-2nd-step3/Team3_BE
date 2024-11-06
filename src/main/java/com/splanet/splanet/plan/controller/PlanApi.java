package com.splanet.splanet.plan.controller;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/plans")
@Tag(name = "Plans", description = "플랜 관련 API")
public interface PlanApi {

    @PostMapping
    @Operation(summary = "플랜 생성", description = "로그인한 사용자가 새로운 플랜을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 필수 필드가 누락되었거나 유효하지 않은 값이 포함되어 있습니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 유효한 JWT 토큰이 필요합니다.", content = @Content)
    })
    ResponseEntity<PlanResponseDto> createPlan(
            @Parameter(description = "JWT 인증을 통해 추출된 사용자 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "생성할 플랜의 정보", required = true) @RequestBody PlanRequestDto requestDto);

    @GetMapping("/{planId}")
    @Operation(summary = "플랜 조회", description = "특정 플랜의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다. 제공된 ID에 해당하는 플랜이 존재하지 않습니다.", content = @Content)
    })
    ResponseEntity<PlanResponseDto> getPlan(
            @Parameter(description = "조회할 플랜의 ID", required = true, example = "1") @PathVariable Long planId);

    @GetMapping
    @Operation(summary = "전체 플랜 조회", description = "로그인한 사용자의 전체 플랜 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 목록이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 유효한 JWT 토큰이 필요합니다.", content = @Content)
    })
    ResponseEntity<List<PlanResponseDto>> getAllPlans(
            @Parameter(description = "JWT 인증을 통해 추출된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @PutMapping("/{planId}")
    @Operation(summary = "플랜 수정", description = "기존 플랜의 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다. 제공된 ID에 해당하는 플랜이 존재하지 않습니다.", content = @Content)
    })
    ResponseEntity<PlanResponseDto> updatePlan(
            @Parameter(description = "수정할 플랜의 ID", required = true, example = "1") @PathVariable Long planId,
            @Parameter(description = "수정할 플랜의 정보", required = true) @RequestBody PlanRequestDto requestDto);

    @DeleteMapping("/{planId}")
    @Operation(summary = "플랜 삭제", description = "특정 플랜을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다. 제공된 ID에 해당하는 플랜이 존재하지 않습니다.", content = @Content)
    })
    ResponseEntity<Void> deletePlan(
            @Parameter(description = "삭제할 플랜의 ID", required = true, example = "1") @PathVariable Long planId);

    @PostMapping("/save-preview/{deviceId}/{groupId}")
    @Operation(summary = "프리뷰 플랜 저장", description = "해당 디바이스 ID와 그룹 ID의 모든 플랜 카드를 사용자의 플랜으로 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프리뷰 플랜이 성공적으로 저장되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 프리뷰 플랜을 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<List<PlanResponseDto>> savePreviewToPlans(
            @Parameter(description = "JWT 인증을 통해 추출된 사용자 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "디바이스 ID", required = true) @PathVariable String deviceId,
            @Parameter(description = "그룹 ID", required = true) @PathVariable String groupId);

}