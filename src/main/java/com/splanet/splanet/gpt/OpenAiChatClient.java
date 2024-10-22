package com.splanet.splanet.gpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class OpenAiChatClient {

    private final WebClient webClient;

    @Value("${spring.ai.gpt.api-key}")
    private String apiKey;

    public OpenAiChatClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    public String call(String message) {
        Message userMessage = new Message(message); // 메시지 객체 생성
        Prompt prompt = new Prompt(List.of(userMessage)); // Message 객체를 리스트로 감싸서 Prompt 생성

        ChatResponse response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey) // 프로퍼티에서 가져온 API 키 사용
                .bodyValue(prompt)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block(); // 블록하여 응답을 기다림

        return response != null && response.getChoices() != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "No response";
    }

    public Flux<ChatResponse> stream(Prompt prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey) // 프로퍼티에서 가져온 API 키 사용
                .bodyValue(prompt)
                .retrieve()
                .bodyToFlux(ChatResponse.class); // Stream the response
    }
}