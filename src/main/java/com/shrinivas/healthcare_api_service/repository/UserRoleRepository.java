package com.shrinivas.healthcare_api_service.repository;

import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Role;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUser(AppUser user);

    @Query("""
            SELECT ur
            FROM UserRole ur
            JOIN FETCH ur.role
            WHERE ur.user = :user
            """)
    List<UserRole> findByUserWithRole(@Param("user") AppUser user);

    boolean existsByUserAndRole(AppUser systemAdmin, Role systemAdminRole);
}
