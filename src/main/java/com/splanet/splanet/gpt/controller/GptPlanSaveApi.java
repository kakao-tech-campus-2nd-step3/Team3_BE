package com.splanet.splanet.gpt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/gpt/plan")
@Tag(name = "GPT-Plan save", description = "GPT로 생성된 플랜을 저장한다.")
public interface GptPlanSaveApi {

    @PostMapping("/save")
    @Operation(summary = "푸시 알림 테스트", description = "해당 유저에게 테스트 알림을 전송합니다. (사전에 FCM 토큰 발급 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 요청 데이터 형식이나 내용이 올바르지 않습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. 인증이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다. 해당 작업을 수행할 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    ResponseEntity<Void> saveGptResponsePlans(@RequestBody String planGroupResponse);
}
