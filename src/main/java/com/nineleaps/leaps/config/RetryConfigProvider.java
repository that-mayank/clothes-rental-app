package com.nineleaps.leaps.config;

import io.github.resilience4j.retry.Retry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RetryConfigProvider {

    @Bean
    public Retry retry() {
        return Retry.of("uploadFileRetry", ExponentialBackoff.buildRetryConfig(3, 1000));
    }
}
