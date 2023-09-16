package com.nineleaps.leaps.config.amazons3;

import io.github.resilience4j.retry.RetryConfig;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class ExponentialBackoff {
    private ExponentialBackoff() {
        // Private constructor to prevent instantiation
    }

    public static RetryConfig buildRetryConfig(int maxAttempts, long waitDuration) {
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDuration))
                .retryExceptions(S3Exception.class, IOException.class, TimeoutException.class)  // Add specific exceptions if needed
                .build();
    }
}
