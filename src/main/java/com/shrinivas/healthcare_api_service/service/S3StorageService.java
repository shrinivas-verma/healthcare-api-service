package com.shrinivas.healthcare_api_service.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.nio.file.Path;


@Service
@Getter
public class S3StorageService {
    private final S3TransferManager s3TransferManager;
    private final S3AsyncClient s3AsyncClient;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3StorageService(S3TransferManager s3TransferManager,
                            S3AsyncClient s3AsyncClient,
                            S3Client s3Client) {
        this.s3TransferManager = s3TransferManager;
        this.s3AsyncClient=s3AsyncClient;
        this.s3Client=s3Client;
    }

    public boolean checkBucketAccess() {

        HeadBucketRequest request = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.headBucket(request);

        return true;
    }

    public void uploadFile(
            Path source,
            String s3ObjectKey,
            String contentType) {

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3ObjectKey)
                        .contentType(contentType)
                        .build();

        UploadFileRequest uploadRequest =
                UploadFileRequest.builder()
                        .putObjectRequest(putObjectRequest)
                        .source(source)
                        .build();

        s3TransferManager.uploadFile(uploadRequest)
                .completionFuture()
                .join();
    }

    public void deleteFile(String s3ObjectKey) {

        s3AsyncClient.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3ObjectKey)
                        .build()
        ).join();
    }

}
