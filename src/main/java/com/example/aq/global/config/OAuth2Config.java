package com.example.aq.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@ConfigurationProperties(prefix = "oauth2")
@Getter
@Setter
public class OAuth2Config {
    
    private Kakao kakao = new Kakao();
    
    @Getter
    @Setter
    public static class Kakao {
        private String clientId;
        private String jwkSetUri;
        private String issuer;
    }
    
    @Bean
    public JwtDecoder kakaoJwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(kakao.getJwkSetUri())
                .build();
    }
}
