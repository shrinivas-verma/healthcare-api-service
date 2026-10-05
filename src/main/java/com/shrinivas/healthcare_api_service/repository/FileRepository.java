package com.shrinivas.healthcare_api_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository implements JpaRepository<User,Integer> {
}
