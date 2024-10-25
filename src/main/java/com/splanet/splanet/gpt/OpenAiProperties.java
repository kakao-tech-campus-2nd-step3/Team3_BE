package com.splanet.splanet.gpt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "gpt-api-key")
@Configuration
public class OpenAiProperties {
    private String apiKey;
}