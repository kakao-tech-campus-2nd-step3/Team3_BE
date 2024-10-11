package com.splanet.splanet.friendRequest.controller;

import com.splanet.splanet.friendRequest.dto.FriendRequestRequest;
import com.splanet.splanet.friendRequest.dto.FriendRequestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/friends/requests")
@Tag(name = "FriendRequest", description = "친구 요청 관련 API")
public interface FriendRequestApi {

    @PostMapping
    @Operation(summary = "친구 요청", description = "특정 사용자에게 친구 요청을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청이 성공적으로 전송되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 유저 ID)."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    ResponseEntity<FriendRequestResponse> sendFriendRequest(
            @Parameter(description = "친구 요청을 보낼 사용자 ID", required = true) @RequestBody FriendRequestRequest request);

    @PostMapping("/{request_id}/accept")
    @Operation(summary = "친구 요청 수락", description = "친구 요청 수락")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 성공적으로 수락되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 유저 ID)."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "404", description = "친구 요청을 찾을 수 없습니다.")
    })
    ResponseEntity<FriendRequestResponse> acceptFriendRequest(
            @Parameter(description = "친구 요청 ID", required = true) @PathVariable Long requestId);

    @PostMapping("/{request_id}/reject")
    @Operation(summary = "친구 요청 거절", description = "친구 요청 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청이 성공적으로 거절되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "404", description = "친구 요청을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "친구 요청을 찾을 수 없습니다.")
    })
    ResponseEntity<FriendRequestResponse> rejectFriendRequest(
            @Parameter(description = "친구 요청 ID", required = true) @PathVariable Long requestId);

    @GetMapping("/received")
    @Operation(summary = "친구 요청 목록 조회 (받은 요청)", description = "사용자가 받은 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 친구 요청 목록이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @GetMapping("/sent")
    @Operation(summary = "친구 요청 목록 조회 (보낸 요청)", description = "사용자가 보낸 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보낸 친구 요청 목록이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    ResponseEntity<List<FriendRequestResponse>> getSentRequests(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);
}