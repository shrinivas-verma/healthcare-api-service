package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
