package com.splanet.splanet.gpt;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GptService {

    private final OpenAiApi openAiApi;

    // GPT 모델과 기본 파라미터 설정
    private static final String GPT_MODEL = OpenAiApi.ChatModel.GPT_4_O_MINI.getValue();
    private static final double RESPONSE_TEMPERATURE = 0.8;

    public GptService(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    public String generateResponse(String inputMessage) {
        // 사용자의 입력을 기반으로 메시지 객체 생성
        OpenAiApi.ChatCompletionMessage userMessage = new OpenAiApi.ChatCompletionMessage(
                inputMessage, OpenAiApi.ChatCompletionMessage.Role.USER);

        // 사용자 메시지를 포함하여 ChatCompletionRequest 객체 생성
        OpenAiApi.ChatCompletionRequest chatRequest = new OpenAiApi.ChatCompletionRequest(
                List.of(userMessage),
                GPT_MODEL,
                RESPONSE_TEMPERATURE
        );

        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(chatRequest);
        OpenAiApi.ChatCompletion chatCompletion = responseEntity.getBody();

        if (chatCompletion != null && !chatCompletion.choices().isEmpty()) {
            return chatCompletion.choices().get(0).message().content();
        } else {
            return "응답이 유효하지 않습니다.";
        }
    }
}