package com.splanet.splanet.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

@Service
public class GptService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GptService(RestTemplate restTemplate, GptProperties gptProperties) {
        this.restTemplate = restTemplate;
        this.apiKey =  gptProperties.getApiKey();
    }

    public String callGpt(String prompt) {
        String gptApiUrl = "https://api.openai.com/v1/chat/completions"; // GPT API URL

        GptRequest gptRequest = new GptRequest();
        gptRequest.setModel("gpt-4o-mini"); // 모델 이름 설정

        // 메시지 배열 생성
        GptRequest.Message message = new GptRequest.Message();
        message.setRole("user"); // 역할 설정
        message.setContent(prompt); // 프롬프트 내용 설정

        List<GptRequest.Message> messages = new ArrayList<>();
        messages.add(message);
        gptRequest.setMessages(messages); // 메시지 설정

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<GptRequest> requestEntity = new HttpEntity<>(gptRequest, headers);

        // GPT API 호출
        String response = restTemplate.postForObject(gptApiUrl, requestEntity, String.class);
        return response != null ? response : "Error: No response from GPT API.";
    }
}