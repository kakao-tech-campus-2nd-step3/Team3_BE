package com.splanet.splanet.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gpt")
public class GptController {

    private final GptService gptService;

    @Autowired
    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping
    public GptResponse callGpt(@RequestBody GptRequest gptRequest) {
        // messages 리스트에서 첫 번째 메시지의 content를 가져옵니다.
        List<GptRequest.Message> messages = gptRequest.getMessages();
        String prompt = messages.isEmpty() ? "" : messages.get(0).getContent();

        // GptService 호출
        String response = gptService.callGpt(prompt);

        // 응답 설정
        GptResponse gptResponse = new GptResponse();
        gptResponse.setResponse(response);
        return gptResponse;
    }
}
