package com.splanet.splanet.gpt;

import lombok.Getter;
import java.util.List;

@Getter
public class ChatResponse {
    private List<Choice> choices; // Choice 객체 리스트
}