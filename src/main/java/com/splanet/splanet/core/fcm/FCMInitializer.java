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
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.info("FCM initialization error occurred.");
            log.error("FCM error message : " + e.getMessage());
        }
    }
}