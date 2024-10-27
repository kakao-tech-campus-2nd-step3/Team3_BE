package com.splanet.splanet.chat;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GptController implements GptApi {

    private final GptService gptService;

    @Override
    public ResponseEntity<GptResponse> callGpt(@RequestBody GptRequest gptRequest) {
        // messages 리스트에서 첫 번째 메시지의 content를 가져옵니다.
        List<GptRequest.Message> messages = gptRequest.getMessages();
        String prompt = messages.isEmpty() ? "" : messages.get(0).getContent();

        // GptService 호출
        String response = gptService.callGpt(prompt);

        // 응답 설정
        GptResponse gptResponse = new GptResponse();
        gptResponse.setResponse(response);

        // ResponseEntity로 응답 반환
        return ResponseEntity.ok(gptResponse);
    }
}