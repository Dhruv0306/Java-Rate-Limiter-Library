package com.github.dhruv0306.throttle4j.spring;

import com.github.dhruv0306.throttle4j.core.RateLimiter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

// Activates only when throttle4j.enabled=true in the application config
@AutoConfiguration
@ConditionalOnProperty(prefix = "throttle4j", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(Throttle4jProperties.class)
public class Throttle4jAutoConfiguration {

    // Creates a RateLimiter bean using the fluent builder and bound properties
    @Bean
    public RateLimiter rateLimiter(Throttle4jProperties properties) {
        return RateLimiter.builder()
                .algorithm(properties.getAlgorithm())
                .capacity(properties.getCapacity())
                .refillRate(properties.getRefillTokens(), properties.getRefillDuration())
                .build();
    }
}