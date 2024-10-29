package com.splanet.splanet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) { //인터페이스 WebMvcConfigurer 상속
        registry.addMapping("/**") //모든 경로를 허용해줄것이므로
                .allowedOrigins("http://localhost:5173", "https://splanet.co.kr") //리소스 공유 허락할 origin 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE") //모든 메소드를 허용
                .allowCredentials(true);
    }
}