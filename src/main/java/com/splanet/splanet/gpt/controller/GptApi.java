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

@Tag(name = "GPT", description = "GPT 관련 API")
@RequestMapping("/api/gpt")
public interface GptApi {

    @PostMapping("/trial/strong")
    @Operation(summary = "강 레벨 GPT 요청 보내기 - 비회원", description = "비회원의 강 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForTrialStrong(@RequestBody GptRequest gptRequest, @RequestParam String deviceId);

    @PostMapping("/trial/moderate")
    @Operation(summary = "중 레벨 GPT 요청 보내기 - 비회원", description = "비회원의 중 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForTrialModerate(@RequestBody GptRequest gptRequest, @RequestParam String deviceId);

    @PostMapping("/trial/light")
    @Operation(summary = "약 레벨 GPT 요청 보내기 - 비회원", description = "비회원의 약 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForTrialLight(@RequestBody GptRequest gptRequest, @RequestParam String deviceId);

    @PostMapping("/member/strong")
    @Operation(summary = "강 레벨 GPT 요청 보내기 - 회원", description = "회원의 강 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForMemberStrong(@RequestBody GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId);

    @PostMapping("/member/moderate")
    @Operation(summary = "중 레벨 GPT 요청 보내기 - 회원", description = "회원의 중 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForMemberModerate(@RequestBody GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId);

    @PostMapping("/member/light")
    @Operation(summary = "약 레벨 GPT 요청 보내기 - 회원", description = "회원의 약 레벨 GPT 요청을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> callGptForMemberLight(@RequestBody GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId);

    @GetMapping("/generate-device-id")
    @Operation(summary = "새로운 Device ID 생성", description = "새로운 deviceId를 생성하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deviceId가 성공적으로 생성되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    ResponseEntity<String> generateDeviceId();
}
