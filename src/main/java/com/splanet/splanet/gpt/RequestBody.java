package com.splanet.splanet.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestBody {
    private String model; // 모델 이름
    private List<Message> messages; // 메시지 리스트
}