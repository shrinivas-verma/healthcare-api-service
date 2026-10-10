package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.dto.OrgDto;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Organization findByCode(String code);

}
