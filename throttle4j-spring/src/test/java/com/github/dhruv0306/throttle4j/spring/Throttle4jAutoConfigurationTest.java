package com.github.dhruv0306.throttle4j.spring;

import com.github.dhruv0306.throttle4j.core.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Boots a test context with throttle4j properties enabled
@SpringBootTest(properties = {
        "throttle4j.enabled=true",
        "throttle4j.algorithm=TOKEN_BUCKET",
        "throttle4j.capacity=50"
})
class Throttle4jAutoConfigurationTest {

    // Minimal Spring Boot app for the test context
    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }

    @Autowired
    private RateLimiter rateLimiter;

    @Test
    void rateLimiterBeanIsCreatedWithCorrectCapacity() {
        // Verify the bean was created by autoconfiguration
        assertNotNull(rateLimiter);
        // Verify it respects the configured capacity of 50
        assertEquals(50, rateLimiter.getAvailablePermits());
    }
}