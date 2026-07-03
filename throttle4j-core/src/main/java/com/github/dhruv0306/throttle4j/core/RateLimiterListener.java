package com.github.dhruv0306.throttle4j.core;

public interface RateLimiterListener {

    void onPermitAcquired();

    void onPermitRejected();

    void onWait(long nanos);
}