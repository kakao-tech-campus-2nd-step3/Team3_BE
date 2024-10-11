package com.splanet.splanet.previewplan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("planCard")
public class PlanCard {

    @Id
    private String customKey;

    private String deviceId;
    private String groupId;
    private String cardId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long expiration = 1L;

    public static String generateId() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}

