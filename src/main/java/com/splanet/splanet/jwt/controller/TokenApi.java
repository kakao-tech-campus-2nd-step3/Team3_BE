package com.splanet.splanet.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/token")
@Tag(name = "Token", description = "토큰 관련 API")
public interface TokenApi {

    @PostMapping("/issue")
    @Operation(summary = "테스트 유저 토큰 발급", description = "테스트용 유저에게 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰이 성공적으로 발급되었습니다.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<String> issueToken(
            @Parameter(description = "유저 ID", required = true) @RequestParam Long userId);

    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰을 통해 액세스 토큰 재발급", description = "유효한 리프레시 토큰을 통해 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰이 성공적으로 재발급되었습니다.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 리프레시 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<String> refreshAccessToken(
            @Parameter(description = "리프레시 토큰", required = true) @RequestParam String refreshToken,
            @Parameter(description = "디바이스 ID", required = true) @RequestParam String deviceId);

    @DeleteMapping("/delete")
    @Operation(summary = "리프레시 토큰 삭제", description = "리프레시 토큰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리프레시 토큰이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 리프레시 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<Void> deleteRefreshToken(
            @Parameter(description = "유저 ID", required = true) @RequestParam Long userId,
            @Parameter(description = "디바이스 ID", required = true) @RequestParam String deviceId);
}
