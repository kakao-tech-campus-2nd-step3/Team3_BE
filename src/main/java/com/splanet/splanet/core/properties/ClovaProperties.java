package com.splanet.splanet.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "clova")
public class ClovaProperties {
    private String clientId;
    private String clientSecret;
    private String url;
    private String language;
}
