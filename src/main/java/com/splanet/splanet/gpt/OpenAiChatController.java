//package com.splanet.splanet.gpt;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/api/gpt")
//public class OpenAiChatController {
//
//    private final OpenAiChatClient openAiChatClient;
//
//    public OpenAiChatController(OpenAiChatClient openAiChatClient) {
//        this.openAiChatClient = openAiChatClient;
//    }
//
//    @PostMapping
//    public Mono<ScheduleResponse> createSchedule(@RequestBody ScheduleRequest scheduleRequest) {
//        // createSchedule 메소드를 호출하여 Mono<ScheduleResponse>를 반환합니다.
//        return Mono.fromCallable(() -> openAiChatClient.createSchedule(scheduleRequest));
//    }
//}