package com.splanet.splanet.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class OpenAiChatClient {

    private final WebClient webClient;
    private final OpenAiProperties openAiProperties;
    private final SchedulePromptGenerator promptGenerator;

    public OpenAiChatClient(WebClient.Builder webClientBuilder, OpenAiProperties openAiProperties, SchedulePromptGenerator promptGenerator) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.openAiProperties = openAiProperties;
        this.promptGenerator = new SchedulePromptGenerator();
    }

    // 스케줄 생성 요청 처리 메소드
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) throws JsonProcessingException {
        String jsonResponse = call(scheduleRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, ScheduleResponse.class);
    }

    private String call(ScheduleRequest scheduleRequest) throws JsonProcessingException {
        String jsonRequest = new ObjectMapper().writeValueAsString(new RequestBody("gpt-4o-mini", List.of(
                new Message("user", promptGenerator.generateSchedulePrompt(scheduleRequest))
        )));

        // OpenAI API 호출
        String responseJson = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .bodyValue(jsonRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return responseJson;
    }

    // 스트리밍 메소드
    public Flux<ScheduleResponse> stream(Prompt prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .bodyValue(prompt)
                .retrieve()
                .bodyToFlux(ScheduleResponse.class);
    }
}