package com.splanet.splanet.core.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class FCMInitializer {

    private static final String FIREBASE_CONFIG_PATH = "splanet-firebase.json";

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream());
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(googleCredentials)
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp 초기화 완료");
            } else {
                log.info("FirebaseApp이 이미 초기화되었습니다.");
            }
        } catch (IOException e) {
            log.error("FCM 초기화 오류 발생: " + e.getMessage());
        }
    }
}
