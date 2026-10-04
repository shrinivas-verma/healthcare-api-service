package com.shrinivas.healthcare_api_service.model;

import com.shrinivas.healthcare_api_service.enums.FileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="file")
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;

    @Column(name = "s3_object_key", nullable = false, length = 1000)
    private String s3ObjectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Column(length = 255)
    private String checksum;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;


}
