package com.splanet.splanet.gpt;

import lombok.Data;

@Data
public class GptRequest {
    private String text;

    // 기본 생성자
    public GptRequest() {}
}
