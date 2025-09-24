package com.example.aq.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AQ - AI 모델 비교 및 프롬프트 공유 플랫폼 API")
                        .description("AQ는 AI 모델 리뷰, 프롬프트 레시피 공유, 모델 비교 기능을 제공하는 REST API입니다")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AQ 팀")
                                .email("contact@aq.com")
                                .url("https://aq.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.aq.com")
                                .description("운영 서버")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
