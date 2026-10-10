package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import com.shrinivas.healthcare_api_service.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
}
