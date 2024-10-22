package com.splanet.splanet.gpt;

import lombok.Getter;

@Getter
public class Message {
    private String content; // 메시지 내용

    public Message(String content) {
        this.content = content;
    }
}