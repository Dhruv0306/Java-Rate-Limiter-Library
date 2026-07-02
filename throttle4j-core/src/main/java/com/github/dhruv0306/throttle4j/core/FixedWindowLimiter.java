package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;

// Temporary stub - full implementation in Step 3
class FixedWindowLimiter implements RateLimiter {
    FixedWindowLimiter(long maxRequests, Duration windowDuration) {
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