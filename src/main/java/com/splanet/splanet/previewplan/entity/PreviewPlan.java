package com.splanet.splanet.previewplan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("previewPlan")
public class PreviewPlan {

    @Id
    private String deviceId;

    private List<String> groupIds;

    @TimeToLive
    private Long expiration;
}
