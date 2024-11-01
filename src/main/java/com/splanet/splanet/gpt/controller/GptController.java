package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.gpt.GptRequest;
import com.splanet.splanet.gpt.service.DeviceIdGenerator;
import com.splanet.splanet.gpt.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GptController implements GptApi {

    private final GptService gptService;

    @Override
    public ResponseEntity<String> callGptForTrial(GptRequest gptRequest, @RequestParam String deviceId) {
        String jsonResponse = gptService.generateResponse(gptRequest.getText(), null, deviceId); // 비회원 호출
        return ResponseEntity.ok(jsonResponse);
    }

    @Override
    public ResponseEntity<String> callGptForMember(GptRequest gptRequest, Long userId, @RequestParam String deviceId) {
        String jsonResponse = gptService.generateResponse(gptRequest.getText(), userId, deviceId); // 회원 호출
        return ResponseEntity.ok(jsonResponse);
    }


    @Override
    public ResponseEntity<String> generateDeviceId() {
        String deviceId = DeviceIdGenerator.generateDeviceId();
        return ResponseEntity.ok(deviceId);
    }
}
