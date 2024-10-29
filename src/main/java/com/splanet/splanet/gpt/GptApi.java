package com.splanet.splanet.gpt;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "GPT", description = "GPT 연결 관련 API")
@RequestMapping("/api/gpt")
public interface GptApi {
    @PostMapping
    @Operation(summary = "GPT 요청 보내기",
            description = "사용자가 요청을 보내면 GPT의 응답을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 응답을 성공적으로 가져왔습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    })
    ResponseEntity<String> callGpt(@RequestBody GptRequest gptRequest);
}