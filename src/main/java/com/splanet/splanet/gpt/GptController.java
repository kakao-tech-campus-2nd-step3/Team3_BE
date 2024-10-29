package com.splanet.splanet.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GptController implements GptApi {

    private final GptService gptService;

    @Override
    public ResponseEntity<String> callGpt(@RequestBody GptRequest gptRequest) {
        try {
            // GptRequest에서 사용자 입력을 가져옴
            String userInput = gptRequest.getText();
            String jsonResponse = gptService.callGptApi(userInput);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            // 오류 발생 시 적절한 응답을 반환
            return ResponseEntity.status(500).body("서버 오류 발생: " + e.getMessage());
        }
    }
}