package com.splanet.splanet.gpt.service;

import java.util.UUID;

public class DeviceIdGenerator {

    public static String generateDeviceId() {
        return UUID.randomUUID().toString().split("-")[0]; // 첫 번째 단락만 반환
    }
}
