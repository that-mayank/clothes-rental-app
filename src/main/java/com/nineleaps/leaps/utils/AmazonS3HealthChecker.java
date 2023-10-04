package com.nineleaps.leaps.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Component
@Slf4j
public class AmazonS3HealthChecker implements ApplicationRunner {

    @Autowired
    private S3Client s3Client;

    @Override
    public void run(ApplicationArguments args){
        checkS3Health();
        checkS3BucketHealth();
    }

    public void checkS3Health() {
        try {
            // Perform a simple operation to check S3 connectivity
            s3Client.listBuckets();
            log.info("Amazon S3 is healthy");
        } catch (Exception e) {
            log.error("Amazon S3 health check failed: " + e.getMessage());
        }
    }

    public void checkS3BucketHealth(){

        s3Client.listBuckets();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        if(listBucketsResponse!= null) {
            for (Bucket bucket : listBucketsResponse.buckets()) {
                String bucketName = bucket.name();
                log.info("Bucket: " + bucketName);

                // Check bucket accessibility

                GetBucketLocationResponse locationResponse = s3Client.getBucketLocation(GetBucketLocationRequest.builder()
                        .bucket(bucketName)
                        .build());

                log.info("Location: " + locationResponse.locationConstraintAsString());


            }
        }

    }
}
