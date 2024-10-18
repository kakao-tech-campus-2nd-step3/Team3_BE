//package com.splanet.splanet.stt.controller;
//
//import com.splanet.splanet.stt.service.ClovaSpeechService;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.bind.annotation.RestController;
//
//@Controller
//@RequestMapping("/api/stt")
//public class SpeechRecognitionController {
//
//    private final ClovaSpeechService clovaSpeechService;
//
//    public SpeechRecognitionController(ClovaSpeechService clovaSpeechService) {
//        this.clovaSpeechService = clovaSpeechService;
//    }
//
//    @PostMapping(consumes = "multipart/form-data")
//    public String recognizeSpeech(@RequestParam("file") MultipartFile file) {
//        try {
//            return clovaSpeechService.recognize(file.getBytes());
//        } catch (Exception e) {
//            throw new RuntimeException("파일 처리 중 오류 발생: " + e.getMessage());
//        }
//    }
//}
