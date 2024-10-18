package com.splanet.splanet.stt.service;

import org.springframework.web.multipart.MultipartFile;

public interface ClovaSpeechService {
    String recognize(byte[] audioBytes);
}
