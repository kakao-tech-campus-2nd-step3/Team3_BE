package com.splanet.splanet.friend.controller;

import com.splanet.splanet.friend.dto.FriendResponse;
import com.splanet.splanet.plan.dto.PlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/friends")
@Tag(name = "Friend", description = "친구 관련 API")
public interface FriendApi {

    @GetMapping
    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    ResponseEntity<List<FriendResponse>> getFriends(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @GetMapping("/{friendId}/plans")
    @Operation(summary = "친구 플랜 조회", description = "친구의 공개된 플랜 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구의 공개된 계획이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 친구 ID)."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "404", description = "친구를 찾을 수 없습니다.")
    })
    ResponseEntity<List<PlanResponseDto>> getFriendPlan(
            @Parameter(description = "조회할 친구 ID", required = true) @PathVariable("friendId") Long friendId,
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구 삭제하기", description = "친구 목록에서 삭제합니다.")
    ResponseEntity<Map<String, String>> unfriend(
            @Parameter(description = "삭제할 친구 ID", required = true) @PathVariable("friendId") Long friendId,
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);
}