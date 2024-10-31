package com.splanet.splanet.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GptController implements GptApi {

    private final GptService gptService;

    @Override
    public ResponseEntity<String> callGptForTrial(GptRequest gptRequest) {
        String jsonResponse = gptService.generateResponse(gptRequest.getText(), null); // 비회원 호출
        return ResponseEntity.ok(jsonResponse);
    }

    @Override
    public ResponseEntity<String> callGptForMember(GptRequest gptRequest, Long userId) {
        String jsonResponse = gptService.generateResponse(gptRequest.getText(), userId); // 회원 호출
        return ResponseEntity.ok(jsonResponse);
    }
}
