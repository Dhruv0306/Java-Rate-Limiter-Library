<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Build a Java Rate Limiter Library

**Project Link:** [View Project](https://nextwork.ai/projects/68c073bc-6244-4c9d-8e92-e5ff497de68a)

**Author:** Dhruv Patel  
**Email:** dpatel5469@gmail.com

---

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_6ph0kcuv)

## Publishing a Java Rate Limiter Library to GitHub Packages

### Project overview and goals

In this project, I’m building and deploying a cloud-based application so that I can gain hands-on experience with real-world development, integration, and deployment of scalable systems.

### Automating deployment with GitHub Actions

In this step, I’m publishing my library to GitHub Packages using a CI/CD pipeline so that other developers can easily access, download, and use it in their own projects.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_6ph0kcuv)

### Confirming the published library is consumable

I ran the consumer and saw "Permits available: 10, Acquired: true, Permits after acquire: 9". This confirms that my published library is working correctly, enforcing rate limits as expected, and can be successfully consumed by other projects.

## Setting Up the Multi-Module Maven Project

### Structuring the parent and child modules

In this step, I’m setting up the project environment and necessary tools so that I can begin building and running the application smoothly.

### GitHub Packages groupId requirements

The groupId must match com.github.<username> because GitHub Packages uses this naming convention to associate the package with my GitHub account and ensure it is published and resolved correctly.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_rz9xqlpj)

## Implementing the Core Rate Limiter API and Token Bucket Algorithm

### Designing the fluent builder API

In this step, I’m setting up the core rate limiter API and implementing the token bucket algorithm so that I can control how many requests are allowed and handle concurrent access safely.

### Lock-free concurrency with AtomicLong and CAS

The TokenBucketLimiter uses AtomicLong because it enables lock-free, thread-safe updates using CAS loops, allowing multiple threads to modify the token count efficiently without the overhead and contention caused by synchronized blocks.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_9sfr7aif)

## Implementing Fixed Window and Sliding Window Log Algorithms

### Adding two more production-grade algorithms

In this step, I’m implementing the Fixed Window and Sliding Window Log algorithms so that the library can support multiple rate-limiting strategies for different real-world use cases.

### Thread-safe sliding window with OOM protection

The AtomicInteger is needed because it provides an efficient, thread-safe way to track the current request count without repeatedly traversing the ConcurrentLinkedDeque, reducing overhead and improving performance under high concurrency.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_tog4swtf)

## Building the Spring Boot Auto-Configuration Module

### Creating a zero-config Spring Boot starter

In this step, I’m building the Spring Boot auto-configuration module so that Spring Boot users can easily enable and configure rate limiting through simple YAML properties without manual setup.

### Conditional auto-configuration with @ConditionalOnProperty

The annotation that controls activation is @ConditionalOnProperty, and the property is throttle4j.enabled=true.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_axwnwgb1)

## Secret Mission: Adding a Metrics Listener API

### High-contention observability with LongAdder

In this project extension, LongAdder is better here because it reduces contention under high concurrency by spreading updates across multiple variables, providing better performance than AtomicLong when many threads are updating the counters simultaneously.

![Image](https://nextwork.ai/relaxed_silver_timid_hind/uploads/68c073bc-6244-4c9d-8e92-e5ff497de68a_uwpi0ke2)

## Reflections and Takeaways

### Key tools and concepts learned

The key tools I used include Java, Maven, Spring Boot, GitHub Actions, and GitHub Packages. Key concepts I learnt include rate limiting algorithms (Token Bucket, Fixed Window, Sliding Window), concurrency handling with Atomic types and CAS, lock-free programming, auto-configuration in Spring Boot, and CI/CD pipeline automation.

### Time and challenges

This project took me approximately 2 hrs with the most challenging part being how to wire listener to RateLimit builder.

I did this project today to learn how to design, build, and publish a production-ready rate limiting library with real-world concurrency and deployment practices. Another skill I want to learn is distributed rate limiting using Redis and system design at scale.

---

*Built with [NextWork](https://nextwork.ai) - [View this project](https://nextwork.ai/projects/68c073bc-6244-4c9d-8e92-e5ff497de68a)*
