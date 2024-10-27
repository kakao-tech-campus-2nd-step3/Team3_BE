//package com.splanet.splanet.gpt;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//
//@Component
//public class OpenAiChatClient {
//
//    @Value("${openai.gpt.apiKey}")
//    private String apiKey;
//
//    private final WebClient webClient;
//    private final SchedulePromptGenerator promptGenerator;
//    private final ObjectMapper objectMapper;
//
//    public OpenAiChatClient(WebClient.Builder webClientBuilder,
//                            SchedulePromptGenerator promptGenerator,
//                            ObjectMapper objectMapper) {
//        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
//        this.promptGenerator = promptGenerator;
//        this.objectMapper = objectMapper;
//    }
//
//    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
//        try {
//            String jsonResponse = call(scheduleRequest);
//            return objectMapper.readValue(jsonResponse, ScheduleResponse.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to convert JSON response to ScheduleResponse", e);
//        }
//    }
//
//    private String call(ScheduleRequest scheduleRequest) throws JsonProcessingException {
//        String jsonRequest = objectMapper.writeValueAsString(new RequestBody(
//                "gpt-4o-mini",
//                List.of(new Message("user", promptGenerator.generateSchedulePrompt(scheduleRequest)))
//        ));
//
//        return webClient.post()
//                .uri("/chat/completions")
//                .header("Authorization", "Bearer " + apiKey)
//                .bodyValue(jsonRequest)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//
//    public Flux<ScheduleResponse> stream(Message message) {
//        return webClient.post()
//                .uri("/chat/completions")
//                .header("Authorization", "Bearer " + apiKey)
//                .bodyValue(message)
//                .retrieve()
//                .bodyToFlux(ScheduleResponse.class);
//    }
//}