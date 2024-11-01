package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.gpt.GptRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "GPT", description = "GPT 연결 관련 API")
@RequestMapping("/api/gpt")
public interface GptApi {

    @PostMapping("/trial")
    @Operation(summary = "GPT 요청 보내기 - 비회원", description = "비회원의 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForTrial(@RequestBody GptRequest gptRequest, @RequestParam String deviceId);

    @PostMapping("/member")
    @Operation(summary = "GPT 요청 보내기 - 회원", description = "회원의 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForMember(@RequestBody GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId);

    @GetMapping("/generate-device-id")
    @Operation(summary = "새로운 Device ID 생성", description = "새로운 deviceId를 생성하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deviceId가 성공적으로 생성되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> generateDeviceId();
}
