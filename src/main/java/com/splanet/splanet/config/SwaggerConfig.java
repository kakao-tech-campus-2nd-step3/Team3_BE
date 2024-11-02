package com.splanet.splanet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


@OpenAPIDefinition(
        servers = {
                @Server(url = "https://api.splanet.co.kr", description = "splanet https 서버입니다."),
                @Server(url = "http://localhost:8080", description = "splanet local 서버입니다.")
        }
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String key = "Access Token (Bearer)";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(key);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        Components components = new Components()
                .addSecuritySchemes(key, accessTokenSecurityScheme);

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("Splanet")
                .description("Splanet의 API 문서 (Version : Green)")
                .version("1.0.0");
    }
}