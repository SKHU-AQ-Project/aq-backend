package com.example.aq.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    
    private String secretKey;
    
    private Access access = new Access();
    private Refresh refresh = new Refresh();
    
    @Getter
    @Setter
    public static class Access {
        private String expiration;
        private String header;
        
        public long getExpirationAsLong() {
            return Long.parseLong(expiration);
        }
    }
    
    @Getter
    @Setter
    public static class Refresh {
        private String expiration;
        private String header;
        
        public long getExpirationAsLong() {
            return Long.parseLong(expiration);
        }
    }

    @Bean
    public JwtDecoder kakaoJwtDecoder() {
        String kakaoJwkSetUri = "https://kauth.kakao.com/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(kakaoJwkSetUri).build();
    }
}
