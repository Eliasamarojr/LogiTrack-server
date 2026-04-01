package com.logitrack.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        JwtCookieProperties.class,
        CorsProperties.class,
        SeedProperties.class
})
public class AppConfig {
}
