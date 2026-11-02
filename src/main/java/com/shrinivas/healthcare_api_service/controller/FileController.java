package com.shrinivas.healthcare_api_service.controller;

import com.shrinivas.healthcare_api_service.service.S3StorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private S3StorageService s3StorageService;

    public FileController(S3StorageService s3StorageService){
        this.s3StorageService=s3StorageService;
    }

    @PreAuthorize("hasRole('ORG_ADMIN')")
    @PostMapping("/files/upload")
    public void uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("originalFileName") String originalFileName){
        s3StorageService.uploadFile(file,originalFileName);
    }
}
