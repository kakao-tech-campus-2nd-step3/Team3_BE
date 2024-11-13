package com.splanet.splanet.notification.controller;

import com.splanet.splanet.notification.dto.FcmTokenRequest;
import com.splanet.splanet.notification.dto.FcmTokenUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/fcm")
@Tag(name = "FCM", description = "FCM 토큰 관리 API")
public interface FcmTokenApi {

    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록", description = "유저가 FCM 토큰을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<String> registerFcmToken(
            @AuthenticationPrincipal Long userId,
            @RequestBody FcmTokenRequest fcmTokenRequest
    );

    @PutMapping("/update")
    @Operation(summary = "FCM 토큰 설정 수정", description = "알림 설정 및 알림 오프셋을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 설정이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "404", description = "토큰을 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<String> updateFcmTokenSettings(
            @RequestBody FcmTokenUpdateRequest fcmTokenUpdateRequest
    );


    @DeleteMapping("/delete")
    @Operation(summary = "FCM 토큰 삭제", description = "유저의 FCM 토큰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 토큰을 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<String> deleteFcmToken(
            @RequestParam String token
    );
}
