package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

class TokenBucketLimiter implements RateLimiter {

    private final long capacity;
    private final long refillTokens;
    private final long refillNanos;
    private final AtomicLong tokens;
    private final AtomicLong lastRefillTime;
    private final RateLimiterListener listener;

    TokenBucketLimiter(long capacity, long refillTokens, Duration refillDuration, RateLimiterListener listener) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillNanos = refillDuration.toNanos();
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = new AtomicLong(System.nanoTime());
        this.listener = listener;
    }

    @Override
    public boolean tryAcquire() {
        refill();
        while (true) {
            long current = tokens.get();
            if (current <= 0) {
                if (listener != null) listener.onPermitRejected();
                return false;
            }
            if (tokens.compareAndSet(current, current - 1)) {
                if (listener != null) listener.onPermitAcquired();
                return true;
            }
        }
    }

    @Override
    public void acquire() {
        while (!tryAcquire()) {
            LockSupport.parkNanos(refillNanos / refillTokens);
            if (listener != null) listener.onWait(refillNanos / refillTokens);
        }
    }

    @Override
    public long getAvailablePermits() {
        refill();
        return tokens.get();
    }

    private void refill() {
        long now = System.nanoTime();
        long last = lastRefillTime.get();
        long elapsed = now - last;
        if (elapsed < refillNanos) {
            long tokensToAdd = (elapsed * refillTokens) / refillNanos;
            if (tokensToAdd > 0 && lastRefillTime.compareAndSet(last, now)) {
                long current = tokens.get();
                long newTokens = Math.min(capacity, current + tokensToAdd);
                tokens.set(newTokens);
            }
        } else {
            if (lastRefillTime.compareAndSet(last, now)) {
                tokens.set(capacity);
            }
        }
    }
}