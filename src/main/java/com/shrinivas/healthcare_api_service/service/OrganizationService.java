package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.dto.OrgDto;
import com.shrinivas.healthcare_api_service.dto.response.CreateOrganizationResponse;
import com.shrinivas.healthcare_api_service.enums.RoleName;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.model.auth.Role;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import com.shrinivas.healthcare_api_service.repository.OrganizationRepository;
import com.shrinivas.healthcare_api_service.repository.RoleRepository;
import com.shrinivas.healthcare_api_service.repository.UserRepository;
import com.shrinivas.healthcare_api_service.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository orgRepo;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizationService(OrganizationRepository orgRepo,
                               RoleRepository roleRepository,UserRoleRepository userRoleRepository,
                               PasswordEncoder passwordEncoder,UserRepository userRepository) {
        this.orgRepo = orgRepo;
        this.roleRepository=roleRepository;
        this.userRoleRepository=userRoleRepository;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }


    @Transactional
    public CreateOrganizationResponse createOrganization(OrgDto orgDto) {
        Organization org=new Organization();
        org.setCode(orgDto.getCode());
        org.setName(orgDto.getName());
        org.setCreatedAt(LocalDateTime.now());
        org.setEnabled(true);
//        org.setId(UUID.randomUUID());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepo.save(org);

//        generate admin username
        String baseUsername =
                orgDto.getCode().toLowerCase() + "_admin";
        String username = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        LocalDateTime now=LocalDateTime.now() ;
        AppUser admin=new AppUser();
//        admin.setId(UUID.randomUUID());
        admin.setOrganization(org);
        admin.setUsername(username);
        admin.setEmail(orgDto.getEmail());
        admin.setEnabled(false);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);
        Role orgAdminRole=roleRepository.findByName(RoleName.ORG_ADMIN)
                .orElseThrow(() ->
                        new IllegalStateException("Role not configured"));

        // The activation flow will let the admin choose a password.
        // Do not store a plaintext password.
        admin.setPassword(passwordEncoder.encode(
                UUID.randomUUID().toString()
        ));

        userRepository.save(admin);

        UserRole userRole=new UserRole();
//        userRole.setId(UUID.randomUUID() );
        userRole.setRole(orgAdminRole);
        userRole.setUser(admin);

        userRoleRepository.save(userRole);

        CreateOrganizationResponse resp=new CreateOrganizationResponse();
        resp.setAdminEmail(orgDto.getEmail());
        resp.setAdminUsername(username);
        resp.setOrganizationCode(orgDto.getCode());
        resp.setOrganizationId(org.getId());
        resp.setOrganizationName(orgDto.getName());
        return resp;
    }
}
