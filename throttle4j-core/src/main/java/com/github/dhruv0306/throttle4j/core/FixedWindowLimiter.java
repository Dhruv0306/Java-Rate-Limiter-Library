package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

class FixedWindowLimiter implements RateLimiter {

    private final long maxRequests;
    private final long windowNanos;
    private final AtomicLong counter;
    private final AtomicLong windowStart;

    FixedWindowLimiter(long maxRequests, Duration windowDuration) {
        this.maxRequests = maxRequests;
        this.windowNanos = windowDuration.toNanos();
        this.counter = new AtomicLong(0);
        this.windowStart = new AtomicLong(System.nanoTime());
    }

    @Override
    public boolean tryAcquire() {
        long now = System.nanoTime();
        long start = windowStart.get();
        // Reset counter when current window has expired
        if (now - start >= windowNanos) {
            if (windowStart.compareAndSet(start, now)) {
                counter.set(1);
                return true;
            }
        }
        // Optimistically increment, then roll back if over limit
        long current = counter.incrementAndGet();
        if (current <= maxRequests) return true;
        counter.decrementAndGet();
        return false;
    }

    @Override
    public void acquire() {
        // Spin-wait with parkNanos for virtual-thread friendliness
        while (!tryAcquire()) {
            long start = windowStart.get();
            long remaining = windowNanos - (System.nanoTime() - start);
            if (remaining > 0) LockSupport.parkNanos(remaining);
        }
    }

    @Override
    public long getAvailablePermits() {
        long now = System.nanoTime();
        long start = windowStart.get();
        if (now - start >= windowNanos) return maxRequests;
        return Math.max(0, maxRequests - counter.get());
    }
}