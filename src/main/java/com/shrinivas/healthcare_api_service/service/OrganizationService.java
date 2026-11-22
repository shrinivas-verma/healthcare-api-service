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
import com.shrinivas.healthcare_api_service.repository.AppUserRepository;
import com.shrinivas.healthcare_api_service.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository orgRepo;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizationService(OrganizationRepository orgRepo,
                               RoleRepository roleRepository, UserRoleRepository userRoleRepository,
                               PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
        this.orgRepo = orgRepo;
        this.roleRepository=roleRepository;
        this.userRoleRepository=userRoleRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder=passwordEncoder;
    }


    @Transactional
    public CreateOrganizationResponse createOrganization(OrgDto orgDto) {
        Organization org=new Organization();
        org.setCode(orgDto.getCode());
        org.setName(orgDto.getName());
        org.setCreatedAt(Instant.now());
        org.setEnabled(true);
//        org.setId(UUID.randomUUID());
        org.setUpdatedAt(Instant.now());
        orgRepo.save(org);

//        generate admin username
        String baseUsername =
                orgDto.getCode().toLowerCase() + "_admin";
        String username = baseUsername;
        int suffix = 1;

        while (appUserRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        Instant now=Instant.now() ;
        AppUser admin=new AppUser();
//        admin.setId(UUID.randomUUID());
        admin.setOrganization(org);
        admin.setUsername(username);
        admin.setEmail(orgDto.getEmail());
        admin.setEnabled(true);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);
        Role orgAdminRole=roleRepository.findByName(RoleName.ORG_ADMIN)
                .orElseThrow(() ->
                        new IllegalStateException("Role not configured"));

        // The activation flow will let the admin choose a password.
        // Do not store a plaintext password.
        String randomString=UUID.randomUUID().toString();
        admin.setPassword(passwordEncoder.encode(
                randomString
        ));
        System.out.println(randomString);
//        for testing purpose- remove from code
        appUserRepository.save(admin);

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
