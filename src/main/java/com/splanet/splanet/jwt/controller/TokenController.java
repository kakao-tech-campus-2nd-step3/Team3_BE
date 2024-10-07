package com.splanet.splanet.jwt.controller;

import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.jwt.service.TokenService;
import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@Tag(name = "Tokens", description = "토큰 관련 API")
@RequiredArgsConstructor
public class TokenController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @PostMapping("/issue")
    @Operation(summary = "테스트 유저 토큰 발급", description = "테스트용 유저에게 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰이 성공적으로 발급되었습니다.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<String> issueToken(@RequestParam Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        String token = jwtTokenProvider.createAccessToken(user.getId());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰을 통해 액세스 토큰 재발급", description = "유효한 리프레시 토큰을 통해 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰이 성공적으로 재발급되었습니다.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 리프레시 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<String> refreshAccessToken(@RequestParam String refreshToken,
                                                     @RequestParam String deviceId) {
        String newAccessToken = tokenService.regenerateAccessToken(refreshToken, deviceId);
        return ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "리프레시 토큰 삭제", description = "리프레시 토큰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리프레시 토큰이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 리프레시 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<Void> deleteRefreshToken(@RequestParam Long userId, @RequestParam String deviceId) {
        tokenService.deleteRefreshToken(userId, deviceId);
        return ResponseEntity.ok().build();
    }
}
