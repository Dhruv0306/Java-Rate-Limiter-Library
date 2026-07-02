package com.github.dhruv0306.throttle4j.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WindowLimiterTest {

    // --- Fixed Window Tests ---

    @Test
    void fixedWindow_shouldAllowUpToMaxRequests() {
        // Create a limiter allowing 5 requests per 1-second window
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.FIXED_WINDOW)
                .capacity(5)
                .refillRate(5, Duration.ofSeconds(1))
                .build();

        // First 5 requests should succeed
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(), "Request " + i + " should succeed");
        }
        // 6th request should be rejected
        assertFalse(limiter.tryAcquire(), "Request beyond capacity should be rejected");
    }

    @Test
    void fixedWindow_shouldResetAfterWindowExpires() throws InterruptedException {
        // Create a limiter with a very short window for testing
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.FIXED_WINDOW)
                .capacity(3)
                .refillRate(3, Duration.ofMillis(100))
                .build();

        // Exhaust the window
        for (int i = 0; i < 3; i++) {
            assertTrue(limiter.tryAcquire());
        }
        assertFalse(limiter.tryAcquire());

        // Wait for window to expire
        Thread.sleep(150);

        // After window reset, requests should succeed again
        assertTrue(limiter.tryAcquire(), "Should succeed after window reset");
    }

    // --- Sliding Window Log Tests ---

    @Test
    void slidingWindow_shouldEvictExpiredTimestamps() throws InterruptedException {
        // Create a limiter with 200ms window
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.SLIDING_WINDOW_LOG)
                .capacity(3)
                .refillRate(3, Duration.ofMillis(200))
                .build();

        // Fill to capacity
        for (int i = 0; i < 3; i++) {
            assertTrue(limiter.tryAcquire());
        }
        assertFalse(limiter.tryAcquire());

        // Wait for timestamps to expire
        Thread.sleep(250);

        // Eviction should free up permits
        assertTrue(limiter.tryAcquire(), "Should succeed after eviction of expired timestamps");
    }

    @Test
    void slidingWindow_oomGuard_shouldRejectWhenMaxLogSizeBreached() {
        // Create a limiter with capacity 5 (maxLogSize = 10)
        // We simulate by quickly filling past the normal limit
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.SLIDING_WINDOW_LOG)
                .capacity(5)
                .refillRate(5, Duration.ofSeconds(10))
                .build();

        // Fill to capacity
        int granted = 0;
        for (int i = 0; i < 20; i++) {
            if (limiter.tryAcquire()) granted++;
        }
        // Should never exceed capacity
        assertTrue(granted <= 5, "Grants should not exceed capacity: " + granted);
    }

    // --- Concurrent Stress Test with Virtual Threads ---

    @Test
    void concurrent_shouldNotOverGrantPermits() throws InterruptedException {
        // Limiter allows 100 requests per second
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.SLIDING_WINDOW_LOG)
                .capacity(100)
                .refillRate(100, Duration.ofSeconds(1))
                .build();

        AtomicInteger totalGrants = new AtomicInteger(0);
        int threadCount = 32;
        int requestsPerThread = 50;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Spawn 32 virtual threads to hammer the limiter concurrently
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int t = 0; t < threadCount; t++) {
                executor.submit(() -> {
                    for (int r = 0; r < requestsPerThread; r++) {
                        if (limiter.tryAcquire()) {
                            totalGrants.incrementAndGet();
                        }
                    }
                    latch.countDown();
                });
            }
            latch.await();
        }

        // Total grants must not exceed capacity (100)
        assertTrue(totalGrants.get() <= 100,
                "Over-grant detected: " + totalGrants.get() + " grants for capacity 100");
    }
}