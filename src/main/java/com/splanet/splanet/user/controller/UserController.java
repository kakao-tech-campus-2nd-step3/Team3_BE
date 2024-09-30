package com.splanet.splanet.user.controller;

import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.service.UserService;
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

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "유저 정보 관련 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "유저 정보 조회", description = "토큰을 사용하여 현재 로그인한 유저의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보가 성공적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<UserResponseDto> getUserInfo(
            @AuthenticationPrincipal Long userId) {
        UserResponseDto userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/me")
    @Operation(summary = "유저 정보 수정", description = "유저의 닉네임, 프로필 이미지, 프리미엄 여부를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보가 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청. 필드 값이 잘못되었습니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<UserResponseDto> updateUserInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUserInfo(userId, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/me")
    @Operation(summary = "유저 삭제", description = "현재 로그인한 유저의 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
