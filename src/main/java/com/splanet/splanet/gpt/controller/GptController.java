package com.splanet.splanet.gpt.controller;

import com.splanet.splanet.gpt.GptRequest;
import com.splanet.splanet.gpt.service.DeviceIdGenerator;
import com.splanet.splanet.gpt.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GptController implements GptApi {

    private final GptService gptService;

    @Override
    public ResponseEntity<String> callGptForTrialStrong(GptRequest gptRequest, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), null, deviceId, 3);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> callGptForTrialModerate(GptRequest gptRequest, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), null, deviceId, 2);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> callGptForTrialLight(GptRequest gptRequest, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), null, deviceId,1);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> callGptForMemberStrong(GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), userId, deviceId, 3);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> callGptForMemberModerate(GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), userId, deviceId, 2);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> callGptForMemberLight(GptRequest gptRequest, @AuthenticationPrincipal Long userId, @RequestParam String deviceId) {
        String response = gptService.generateResponse(gptRequest.getText(), userId, deviceId, 1);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> generateDeviceId() {
        String deviceId = DeviceIdGenerator.generateDeviceId();
        return ResponseEntity.ok(deviceId);
    }
}
