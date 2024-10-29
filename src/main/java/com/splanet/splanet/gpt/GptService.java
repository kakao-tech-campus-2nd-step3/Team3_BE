package com.splanet.splanet.gpt;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GptService {

    private final OpenAiApi openAiApi;

    public GptService(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    public String callGptApi(String userInput) {
        // 사용자 입력을 직접 메시지로 전송
        OpenAiApi.ChatCompletionMessage userMessage = new OpenAiApi.ChatCompletionMessage(
                userInput, OpenAiApi.ChatCompletionMessage.Role.USER);

        // ChatCompletionRequest 생성 (사용자 메시지 포함)
        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(
                List.of(userMessage),
                OpenAiApi.ChatModel.GPT_4_O_MINI.getValue(),
                0.7  // 온도 설정 (응답의 창의성 정도)
        );

        // OpenAiApi를 통해 요청 보내고 응답 받기
        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(request);
        OpenAiApi.ChatCompletion completion = responseEntity.getBody();

        // 첫 번째 응답의 내용 반환
        if (completion != null && !completion.choices().isEmpty()) {
            return completion.choices().get(0).message().content();
        } else {
            return "응답을 받을 수 없습니다.";
        }
    }
}