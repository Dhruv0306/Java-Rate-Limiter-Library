package com.github.dhruv0306.throttle4j.spring;

import com.github.dhruv0306.throttle4j.core.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

// Binds all "throttle4j.*" properties from application.yml to this class
@ConfigurationProperties(prefix = "throttle4j")
public class Throttle4jProperties {

    private boolean enabled = false;
    private Algorithm algorithm = Algorithm.TOKEN_BUCKET;
    private long capacity = 100;
    private long refillTokens = 10;
    private Duration refillDuration = Duration.ofSeconds(1);

    // Standard getters and setters for Spring Boot property binding
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getRefillTokens() {
        return refillTokens;
    }

    public void setRefillTokens(long refillTokens) {
        this.refillTokens = refillTokens;
    }

    public Duration getRefillDuration() {
        return refillDuration;
    }

    public void setRefillDuration(Duration refillDuration) {
        this.refillDuration = refillDuration;
    }
}