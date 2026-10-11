package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    @Query("""
            SELECT au
            FROM AppUser au
            JOIN FETCH au.organization
            WHERE au.username = :username
            """)
    Optional<AppUser> findByUsernameWithOrganization(@Param("username") String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
