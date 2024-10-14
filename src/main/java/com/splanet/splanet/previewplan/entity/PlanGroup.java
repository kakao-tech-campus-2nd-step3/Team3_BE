package com.splanet.splanet.previewplan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("planGroup")
public class PlanGroup {

    @Id
    private String customKey;

    private String deviceId;
    private String groupId;
    private Set<String> planCardIds;

    @TimeToLive
    private Long expiration;
}
