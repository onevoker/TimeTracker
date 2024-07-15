package com.onevoker.timetracker.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "security-properties", ignoreUnknownFields = false)
public record SecurityPropertiesConfig(
        @Bean
        Jwt jwt,
        @Bean
        ClaimNames claimNames,
        @Bean
        SecurityFilterProperties securityFilterProperties
) {
    public record Jwt(String secretKey, Duration tokenLifetime) {
    }

    public record ClaimNames(String usernameClaimName, String authoritiesClaimName) {
    }

    public record SecurityFilterProperties(List<String> permitAllRequests, String securityUrlPattern) {
    }
}
