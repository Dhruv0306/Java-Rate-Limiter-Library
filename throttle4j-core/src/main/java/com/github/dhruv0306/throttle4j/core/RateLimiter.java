package com.github.dhruv0306.throttle4j.core;

public interface RateLimiter {

    // Non-blocking: returns true if a permit is available, false otherwise
    boolean tryAcquire();

    // Blocking: waits until a permit becomes available (virtual-thread friendly)
    void acquire();

    // Returns the current number of available permits
    long getAvailablePermits();

    // Entry point to the fluent builder API
    static RateLimiterBuilder builder() {
        return new RateLimiterBuilder();
    }
}