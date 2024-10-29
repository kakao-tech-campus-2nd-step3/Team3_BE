package com.splanet.splanet.gpt;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GptService {

    private final OpenAiApi openAiApi;

    // GPT 모델 및 파라미터 기본값 설정
    private static final String MODEL = OpenAiApi.ChatModel.GPT_4_O_MINI.getValue();
    private static final double TEMPERATURE = 0.5;

    public GptService(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    public String callGptApi(String userInput) {
        // 사용자가 입력하면 메시지 생성하기
        OpenAiApi.ChatCompletionMessage userMessage = new OpenAiApi.ChatCompletionMessage(
                userInput, OpenAiApi.ChatCompletionMessage.Role.USER);

        // 사용자 메시지를 포함해서 ChatCompletionRequest 생성
        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(
                List.of(userMessage),
                OpenAiApi.ChatModel.GPT_4_O_MINI.getValue(),
                0.5
        );

        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(request);
        OpenAiApi.ChatCompletion completion = responseEntity.getBody();

        if (completion != null && !completion.choices().isEmpty()) {
            return completion.choices().get(0).message().content();
        } else {
            return "유효한 응답이 없습니다.";
        }
    }
}