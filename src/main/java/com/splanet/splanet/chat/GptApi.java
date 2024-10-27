package com.splanet.splanet.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "GPT", description = "GPT 스케줄 생성 관련 API")
@RequestMapping("/api/gpt")
public interface GptApi {
    @PostMapping
    @Operation(summary = "추천 스케줄 요청", description = "스케줄 정보를 입력하면 최적의 스케줄을 응답으로 제시합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 스케줄을 성공적으로 생성했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 유저 ID)."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    ResponseEntity<GptResponse> callGpt(@RequestBody GptRequest gptRequest);
}