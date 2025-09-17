package com.example.aq.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private String secretKey = "mySecretKey123456789012345678901234567890";
    private Access access = new Access();
    private Refresh refresh = new Refresh();

    @Getter
    @Setter
    public static class Access {
        private String expiration = "3600000";
        private String header = "Authorization";
    }

    @Getter
    @Setter
    public static class Refresh {
        private String expiration = "604800000";
        private String header = "RefreshToken";
    }
}
