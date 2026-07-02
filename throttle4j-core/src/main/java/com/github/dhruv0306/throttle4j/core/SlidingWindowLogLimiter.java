package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;

// Temporary stub - full implementation in Step 3
class SlidingWindowLogLimiter implements RateLimiter {
    SlidingWindowLogLimiter(long maxRequests, Duration windowDuration) {
    }

    @Override
    public boolean tryAcquire() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acquire() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getAvailablePermits() {
        throw new UnsupportedOperationException();
    }
}