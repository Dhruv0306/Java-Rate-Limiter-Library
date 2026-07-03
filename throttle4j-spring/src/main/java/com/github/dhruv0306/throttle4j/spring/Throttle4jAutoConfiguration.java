package com.github.dhruv0306.throttle4j.spring;

import com.github.dhruv0306.throttle4j.core.MetricsCollector;
import com.github.dhruv0306.throttle4j.core.RateLimiter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "throttle4j", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(Throttle4jProperties.class)
public class Throttle4jAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "throttle4j.metrics", name = "enabled", havingValue = "true")
    public MetricsCollector metricsCollector() {
        return new MetricsCollector();
    }

    @Bean
    public RateLimiter rateLimiter(Throttle4jProperties properties,
                                   ObjectProvider<MetricsCollector> metricsProvider) {
        return RateLimiter.builder()
                .algorithm(properties.getAlgorithm())
                .capacity(properties.getCapacity())
                .refillRate(properties.getRefillTokens(), properties.getRefillDuration())
                .listener(metricsProvider.getIfAvailable())
                .build();
    }
}