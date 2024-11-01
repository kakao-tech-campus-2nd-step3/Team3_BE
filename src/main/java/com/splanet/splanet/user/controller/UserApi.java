package com.splanet.splanet.user.controller;

import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
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

@RequestMapping("/api/users")
@Tag(name = "Users", description = "유저 관련 API")
public interface UserApi {

    @GetMapping("/me")
    @Operation(summary = "유저 정보 조회", description = "현재 로그인한 유저의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보가 성공적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<UserResponseDto> getUserInfo(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @PutMapping("/me")
    @Operation(summary = "유저 정보 수정", description = "유저의 닉네임, 프로필 이미지, 프리미엄 여부를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보가 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청. 필드 값이 잘못되었습니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<UserResponseDto> updateUserInfo(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "수정할 유저 정보", required = true) @RequestBody UserUpdateRequestDto requestDto);

    @DeleteMapping("/me")
    @Operation(summary = "유저 삭제", description = "현재 로그인한 유저의 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "JWT 인증으로 전달된 사용자 ID", required = true) @AuthenticationPrincipal Long userId);

    @PostMapping("/create")
    @Operation(summary = "유저 생성", description = "테스트용 새로운 유저를 생성합니다. 닉네임 중복 시 예외가 발생합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "유저가 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "닉네임이 중복되었습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<UserResponseDto> createUser(
            @Parameter(description = "새로운 유저의 닉네임", required = true) @RequestParam String nickname,
            @Parameter(description = "새로운 유저의 프로필 이미지", required = false) @RequestParam(required = false) String profileImage,
            @Parameter(description = "새로운 유저의 프리미엄 여부", required = false) @RequestParam(required = false) Boolean isPremium);

    @GetMapping("/nickname/{user_nickname}")
    @Operation(summary = "닉네임으로 유저 조회", description = "특정 사용자의 닉네임으로 유저 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보가 성공적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    ResponseEntity<UserResponseDto> getUserByNickname(
            @Parameter(description = "검색할 유저의 닉네임", required = true) @PathVariable String user_nickname);
}
