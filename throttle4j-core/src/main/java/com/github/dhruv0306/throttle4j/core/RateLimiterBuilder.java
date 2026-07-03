package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;

public class RateLimiterBuilder {

    private Algorithm algorithm = Algorithm.TOKEN_BUCKET;
    private long capacity = 10;
    private long refillTokens = 10;
    private Duration refillDuration = Duration.ofSeconds(1);
    private RateLimiterListener listener = null;

    // Package-private constructor: only accessible via RateLimiter.builder()
    RateLimiterBuilder() {
    }

    public RateLimiterBuilder algorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public RateLimiterBuilder capacity(long capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        return this;
    }

    public RateLimiterBuilder refillRate(long tokens, Duration duration) {
        if (tokens <= 0) throw new IllegalArgumentException("Refill tokens must be positive");
        if (duration.isZero() || duration.isNegative()) throw new IllegalArgumentException("Duration must be positive");
        this.refillTokens = tokens;
        this.refillDuration = duration;
        return this;
    }

    public RateLimiterBuilder listener(RateLimiterListener listener) {
        this.listener = listener;
        return this;
    }

    public RateLimiter build() {
        return switch (algorithm) {
            case TOKEN_BUCKET -> new TokenBucketLimiter(capacity, refillTokens, refillDuration, listener);
            case FIXED_WINDOW -> new FixedWindowLimiter(capacity, refillDuration, listener);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogLimiter(capacity, refillDuration, listener);
        };
    }
}