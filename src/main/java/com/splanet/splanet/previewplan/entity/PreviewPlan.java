package com.splanet.splanet.previewplan.entity;

import lombok.AllArgsConstructor;
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
@RedisHash("previewPlan")
public class PreviewPlan {

    @Id
    private String deviceId;

    private List<String> groupIds;

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long expiration = 1L;
}
