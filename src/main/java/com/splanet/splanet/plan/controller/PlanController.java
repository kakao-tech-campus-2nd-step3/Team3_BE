package com.splanet.splanet.plan.controller;

import com.splanet.splanet.plan.dto.PlanRequestDto;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import com.splanet.splanet.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "플랜 관리 관련 API")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @Operation(summary = "플랜 생성", description = "새로운 플랜을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content)
    })
    public ResponseEntity<PlanResponseDto> createPlan(
            @AuthenticationPrincipal Long userId,
            @RequestBody PlanRequestDto requestDto) {
        PlanResponseDto responseDto = planService.createPlan(userId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "플랜 조회", description = "특정 플랜의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다.", content = @Content)
    })
    public ResponseEntity<PlanResponseDto> getPlan(
            @PathVariable Long planId) {
        PlanResponseDto responseDto = planService.getPlanById(planId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "전체 플랜 조회", description = "유저의 전체 플랜 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 목록이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content)
    })
    public ResponseEntity<List<PlanResponseDto>> getAllPlans(
            @AuthenticationPrincipal Long userId) {
        List<PlanResponseDto> plans = planService.getAllPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{planId}")
    @Operation(summary = "플랜 수정", description = "기존 플랜의 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다.", content = @Content)
    })
    public ResponseEntity<PlanResponseDto> updatePlan(
            @PathVariable Long planId,
            @RequestBody PlanRequestDto requestDto) {
        PlanResponseDto updatedPlan = planService.updatePlan(planId, requestDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "플랜 삭제", description = "특정 플랜을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "플랜을 찾을 수 없습니다.", content = @Content)
    })
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }
}
