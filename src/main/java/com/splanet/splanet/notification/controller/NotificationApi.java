package com.splanet.splanet.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "푸시 알림 테스트")
public interface NotificationApi {

    @PostMapping("/send/{userId}")
    @Operation(summary = "푸시 알림 테스트", description = "해당 유저에게 테스트 알림을 전송합니다. (사전에 FCM 토큰 발급 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 알림이 성공적으로 전송되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 유저 ID가 유효하지 않거나 형식이 잘못되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다. 알림 전송에 실패하였습니다.")
    })
    ResponseEntity<String> sendTestNotification(@PathVariable Long userId);
}