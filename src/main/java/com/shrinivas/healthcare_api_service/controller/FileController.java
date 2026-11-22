package com.shrinivas.healthcare_api_service.controller;

import com.shrinivas.healthcare_api_service.dto.response.FileUploadResponse;
import com.shrinivas.healthcare_api_service.enums.JobType;
import com.shrinivas.healthcare_api_service.service.FileUploadService;
import com.shrinivas.healthcare_api_service.service.S3StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
public class FileController {

    private final FileUploadService fileUploadService;

    public FileController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PreAuthorize("hasRole('FILE_OPERATOR')")
    @PostMapping(
            value = "/files/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobType") JobType jobType) {

        FileUploadResponse response =
                fileUploadService.receiveFile(file, jobType);

        return ResponseEntity.accepted().body(response);
    }
}
