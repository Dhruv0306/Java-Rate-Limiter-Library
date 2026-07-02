package com.github.dhruv0306.throttle4j.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketLimiterTest {

    @Test
    void shouldConsumeTokensUntilEmpty() {
        // Create a limiter with capacity 5
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.TOKEN_BUCKET)
                .capacity(5)
                .refillRate(5, Duration.ofSeconds(1))
                .build();

        // All 5 permits should succeed
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire());
        }
        // 6th request should be rejected
        assertFalse(limiter.tryAcquire());
    }

    @Test
    void shouldRefillTokensOverTime() throws InterruptedException {
        // Refills 5 tokens every 100ms
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.TOKEN_BUCKET)
                .capacity(5)
                .refillRate(5, Duration.ofMillis(100))
                .build();

        // Drain all tokens
        for (int i = 0; i < 5; i++) {
            limiter.tryAcquire();
        }
        assertFalse(limiter.tryAcquire());

        // Wait for refill period to elapse
        Thread.sleep(150);

        // Tokens should have refilled
        assertTrue(limiter.tryAcquire());
    }

    @Test
    void shouldHandleConcurrentAccessWithoutOverGranting() throws InterruptedException {
        // Capacity 100, slow refill so we can measure grants accurately
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.TOKEN_BUCKET)
                .capacity(100)
                .refillRate(1, Duration.ofSeconds(10))
                .build();

        int threadCount = 16;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger acquired = new AtomicInteger(0);

        // Each of 16 threads tries to acquire 20 permits (320 total attempts)
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 20; j++) {
                    if (limiter.tryAcquire()) {
                        acquired.incrementAndGet();
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        // No more than 100 permits should have been granted (capacity)
        assertTrue(acquired.get() <= 100,
                "Over-granted! Got " + acquired.get() + " but capacity is 100");
        assertTrue(acquired.get() > 0, "Should have granted at least some permits");
    }
}