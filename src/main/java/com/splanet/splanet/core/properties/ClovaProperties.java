package com.splanet.splanet.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "clova.speech")
public class ClovaProperties {
    private String clientSecret;
    private String language;
}
