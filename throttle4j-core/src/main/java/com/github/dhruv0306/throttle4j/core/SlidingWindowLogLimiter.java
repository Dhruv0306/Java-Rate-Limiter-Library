package com.github.dhruv0306.throttle4j.core;

import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

class SlidingWindowLogLimiter implements RateLimiter {

    private final long maxRequests;
    private final long windowNanos;
    private final int maxLogSize;
    private final ConcurrentLinkedDeque<Long> timestamps;
    private final AtomicInteger size;
    private final RateLimiterListener listener;

    SlidingWindowLogLimiter(long maxRequests, Duration windowDuration, RateLimiterListener listener) {
        this.maxRequests = maxRequests;
        this.windowNanos = windowDuration.toNanos();
        this.listener = listener;
        // OOM guard: hard cap prevents unbounded deque growth under malicious load
        this.maxLogSize = (int) Math.min(maxRequests * 2, Integer.MAX_VALUE);
        this.timestamps = new ConcurrentLinkedDeque<>();
        this.size = new AtomicInteger(0);
    }

    @Override
    public boolean tryAcquire() {
        long now = System.nanoTime();
        evictExpired(now);

        // OOM guard: reject immediately if deque exceeds hard safety boundary
        if (size.get() >= maxLogSize) {
            if (listener != null) listener.onPermitRejected();
            return false;
        }

        // Check if within normal rate limit
        if (size.get() >= maxRequests) {
            if (listener != null) listener.onPermitRejected();
            return false;
        }
        timestamps.addLast(now);
        int current = size.incrementAndGet();
        // Roll back if another thread raced past the check
        if (current > maxRequests) {
            timestamps.pollLast();
            size.decrementAndGet();
            if (listener != null) listener.onPermitRejected();
            return false;
        }
        if (listener != null) listener.onPermitAcquired();
        return true;
    }

    @Override
    public void acquire() {
        while (!tryAcquire()) {
            LockSupport.parkNanos(windowNanos / maxRequests);
            if (listener != null) listener.onWait(windowNanos / maxRequests);
        }
    }

    @Override
    public long getAvailablePermits() {
        evictExpired(System.nanoTime());
        return Math.max(0, maxRequests - size.get());
    }

    private void evictExpired(long now) {
        // Remove timestamps that have fallen outside the rolling window
        while (true) {
            Long head = timestamps.peekFirst();
            if (head == null || now - head < windowNanos) break;
            if (timestamps.pollFirst() != null) {
                size.decrementAndGet();
            }
        }
    }
}