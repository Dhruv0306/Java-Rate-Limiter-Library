package com.github.dhruv0306.throttle4j.core;

import java.util.concurrent.atomic.LongAdder;

public class MetricsCollector implements RateLimiterListener {

    private final LongAdder totalGrants = new LongAdder();
    private final LongAdder totalRejections = new LongAdder();
    private final LongAdder totalWaitNanos = new LongAdder();

    @Override
    public void onPermitAcquired() {
        totalGrants.increment();
    }

    @Override
    public void onPermitRejected() {
        totalRejections.increment();
    }

    @Override
    public void onWait(long nanos) {
        totalWaitNanos.add(nanos);
    }

    public long getTotalGrants() {
        return totalGrants.sum();
    }

    public long getTotalRejections() {
        return totalRejections.sum();
    }

    public long getTotalWaitNanos() {
        return totalWaitNanos.sum();
    }

    public void reset() {
        totalGrants.reset();
        totalRejections.reset();
        totalWaitNanos.reset();
    }
}