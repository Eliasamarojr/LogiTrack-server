package com.logitrack.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "logitrack.cors")
public class CorsProperties {

    private List<String> allowedOrigins = new ArrayList<>();

    private List<String> allowedOriginPatterns = new ArrayList<>(
            List.of("http://localhost:*", "http://127.0.0.1:*"));

    private String frontendPublicUrl = "";

    @PostConstruct
    void appendFrontendPublicUrl() {
        if (frontendPublicUrl != null && !frontendPublicUrl.isBlank()) {
            allowedOriginPatterns.add(frontendPublicUrl.trim());
        }
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public void setFrontendPublicUrl(String frontendPublicUrl) {
        this.frontendPublicUrl = frontendPublicUrl == null ? "" : frontendPublicUrl;
    }
}
