package com.splanet.splanet.gpt;

import com.splanet.splanet.core.properties.GptProperties;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GptService {

    private final OpenAiApi openAiApi;
    private final GptProperties gptProperties;

    public GptService(OpenAiApi openAiApi, GptProperties gptProperties) {
        this.openAiApi = openAiApi;
        this.gptProperties = gptProperties;
    }

    private static final double RESPONSE_TEMPERATURE = 0.8;

    public String generateResponse(String inputMessage) {
        // 사용자의 입력을 기반으로 메시지 객체 생성
        OpenAiApi.ChatCompletionMessage userMessage = new OpenAiApi.ChatCompletionMessage(
                inputMessage, OpenAiApi.ChatCompletionMessage.Role.USER);

        // 사용자 메시지를 포함하여 ChatCompletionRequest 객체 생성
        OpenAiApi.ChatCompletionRequest chatRequest = new OpenAiApi.ChatCompletionRequest(
                List.of(userMessage),
                gptProperties.getGptModel(),
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