package com.github.dhruv0306.throttle4j.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsCollectorTest {

    @Test
    void grantsAndRejectionsEqualTotalRequests() throws InterruptedException {
        // Create a metrics collector to track all outcomes
        MetricsCollector metrics = new MetricsCollector();

        // Build a rate limiter with capacity 100 and attach the collector
        RateLimiter limiter = RateLimiter.builder()
                .algorithm(Algorithm.TOKEN_BUCKET)
                .capacity(100)
                .refillRate(100, Duration.ofSeconds(10))
                .listener(metrics)
                .build();

        int totalRequests = 1000;
        int threadCount = 8;

        // Use a latch so all threads start at the same instant
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < totalRequests; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    limiter.tryAcquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Release all threads simultaneously
        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Every request must result in exactly one callback
        assertEquals(totalRequests, metrics.getTotalGrants() + metrics.getTotalRejections(),
                "grants + rejections must equal total requests");
    }
}