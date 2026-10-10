package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.model.auth.Role;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import com.shrinivas.healthcare_api_service.repository.OrganizationRepository;
import com.shrinivas.healthcare_api_service.repository.RoleRepository;
import com.shrinivas.healthcare_api_service.repository.UserRepository;
import com.shrinivas.healthcare_api_service.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SecurityBootstrapService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.admin.username}")
    private String adminUsername;

    @Value("${bootstrap.admin.password}")
    private String adminPassword;

    @Value("${bootstrap.admin.email}")
    private String adminEmail;

    @Value("${bootstrap.admin.organization-name}")
    private String adminOrgName;

    @Value("${bootstrap.admin.organization-code}")
    private String adminOrgCode;

    public SecurityBootstrapService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initialize() {

        // 1. Find the SYSTEM organization, or create it.
        Organization systemOrganization =
                organizationRepository.findByCode(adminOrgCode);

        LocalDateTime now = LocalDateTime.now();

        if (systemOrganization == null) {
            systemOrganization = new Organization();

//            systemOrganization.setId(UUID.randomUUID());
            systemOrganization.setCode(adminOrgCode);
            systemOrganization.setName(adminOrgName);
            systemOrganization.setEnabled(true);
            systemOrganization.setCreatedAt(now);
            systemOrganization.setUpdatedAt(now);

            systemOrganization =
                    organizationRepository.save(systemOrganization);
        }

        // 2. Find the SYSTEM_ADMIN role.
        Role systemAdminRole =
                roleRepository.findByName(RoleName.SYSTEM_ADMIN)
                        .orElseThrow(()-> new IllegalStateException(
                "SYSTEM_ADMIN role not found. Check Flyway role seed data."));


        // 3. Find the bootstrap user, or create it.
        AppUser systemAdmin = userRepository
                .findByUsername(adminUsername)
                .orElse(null);

        if (systemAdmin == null) {
            systemAdmin = new AppUser();

//            systemAdmin.setId(UUID.randomUUID());
            systemAdmin.setUsername(adminUsername);
            systemAdmin.setEmail(adminEmail);
            systemAdmin.setPassword(
                    passwordEncoder.encode(adminPassword));
            systemAdmin.setEnabled(true);
            systemAdmin.setOrganization(systemOrganization);
            systemAdmin.setCreatedAt(now);
            systemAdmin.setUpdatedAt(now);

            systemAdmin = userRepository.save(systemAdmin);
        } else {
            // Do not silently accept a username belonging to another org.
            if (!systemAdmin.getOrganization().getId()
                    .equals(systemOrganization.getId())) {

                throw new IllegalStateException(
                        "Bootstrap username already belongs to another organization.");
            }
        }

        // 4. Ensure the SYSTEM_ADMIN role assignment exists.
        boolean roleAssignmentExists =
                userRoleRepository.existsByUserAndRole(
                        systemAdmin, systemAdminRole);

        if (!roleAssignmentExists) {
            UserRole userRole = new UserRole();

//            userRole.setId(UUID.randomUUID());
            userRole.setUser(systemAdmin);
            userRole.setRole(systemAdminRole);

            userRoleRepository.save(userRole);
        }
    }
}