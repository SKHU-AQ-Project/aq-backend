package com.example.aq.global.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class HttpsConfig {

    @Value("${server.ssl.key-store:}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password:}")
    private String keystorePassword;

    @Value("${server.ssl.key-store-type:PKCS12}")
    private String keystoreType;

    @Value("${server.ssl.key-alias:springboot}")
    private String keyAlias;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return factory -> {
            if (sslEnabled && !keystorePath.isEmpty() && new File(keystorePath).exists()) {
                // HTTPS 포트 8443 추가
                Connector httpsConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                httpsConnector.setPort(8443);
                httpsConnector.setSecure(true);
                httpsConnector.setScheme("https");

                // SSL 설정을 Connector에 직접 설정
                httpsConnector.setProperty("SSLEnabled", "true");
                httpsConnector.setProperty("keystoreFile", keystorePath);
                httpsConnector.setProperty("keystorePass", keystorePassword);
                httpsConnector.setProperty("keystoreType", keystoreType);
                httpsConnector.setProperty("keyAlias", keyAlias);

                factory.addAdditionalTomcatConnectors(httpsConnector);
            }
        };
    }
}
