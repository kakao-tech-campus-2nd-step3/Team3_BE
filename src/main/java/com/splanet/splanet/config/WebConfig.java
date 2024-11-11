package com.splanet.splanet.config;

import com.splanet.splanet.log.interceptor.ApiLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final ApiLoggingInterceptor apiLoggingInterceptor;

  @Autowired
  public WebConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
    this.apiLoggingInterceptor = apiLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(apiLoggingInterceptor).addPathPatterns("/**");
  }
}