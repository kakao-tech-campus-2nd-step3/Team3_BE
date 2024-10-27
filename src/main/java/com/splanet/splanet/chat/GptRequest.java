package com.splanet.splanet.chat;

import lombok.Data;

import java.util.List;

@Data
public class GptRequest {
    private String model;
    private List<Message> messages;

    // Message 클래스 정의
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}