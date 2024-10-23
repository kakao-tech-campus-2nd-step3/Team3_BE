package com.splanet.splanet.gpt;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class OpenAiChatClient {

    private final WebClient webClient;
    private final OpenAiProperties openAiProperties;

    public OpenAiChatClient(WebClient.Builder webClientBuilder, OpenAiProperties openAiProperties) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.openAiProperties = openAiProperties;
    }

    public String call(String message) {
        Message userMessage = new Message(message);
        Prompt prompt = new Prompt(List.of(userMessage));

        ChatResponse response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .bodyValue(prompt)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        return response != null && response.getChoices() != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "No response";
    }

    public Flux<ChatResponse> stream(Prompt prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .bodyValue(prompt)
                .retrieve()
                .bodyToFlux(ChatResponse.class);
    }
}