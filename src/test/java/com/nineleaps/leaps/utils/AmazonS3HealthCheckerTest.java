package com.nineleaps.leaps.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;
import static org.mockito.Mockito.*;

@Slf4j

class AmazonS3HealthCheckerTest {

    @Mock
    private S3Client s3Client;


    @InjectMocks
    private AmazonS3HealthChecker s3HealthChecker;

    // Initialize mocks
    public AmazonS3HealthCheckerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckS3Health() {
        // Mock S3Client to throw an exception when listBuckets is called
        doThrow(new RuntimeException("Simulated S3 exception")).when(s3Client).listBuckets();

        // Call the method under test
        s3HealthChecker.checkS3Health();

    }





}
