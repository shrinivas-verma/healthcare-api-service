package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {
}
