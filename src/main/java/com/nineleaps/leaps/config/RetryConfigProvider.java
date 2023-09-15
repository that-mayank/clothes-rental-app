package com.nineleaps.leaps.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class RetryConfigProvider{

    @Bean
    public Retry retry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)  // Maximum number of retry attempts
                .waitDuration(Duration.ofSeconds(1))  // Wait duration between retries
                .retryExceptions(S3Exception.class, IOException.class, TimeoutException.class)
                .build();
        return Retry.of("uploadFileRetry", retryConfig);
    }
}

