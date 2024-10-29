package com.splanet.splanet.gpt;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt")
public class GptController {

    private final GptService gptService;

    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping
    public String askGpt(@RequestBody GptRequest gptRequest) {
        return gptService.callGptApi(gptRequest.getText());
    }
}