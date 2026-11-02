package com.shrinivas.healthcare_api_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class S3StorageServiceTest {

    @Autowired
    private S3StorageService s3StorageService;

    @Test
    void shouldConnectToS3Bucket() {

        s3StorageService.checkBucketAccess();

    }
}