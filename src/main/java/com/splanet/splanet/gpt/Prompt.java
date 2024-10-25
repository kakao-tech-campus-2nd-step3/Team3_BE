package com.splanet.splanet.gpt;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor // 모든 필드를 사용하는 생성자를 자동 생성
public class Prompt {
    private List<Message> messages; // 메시지 목록
}