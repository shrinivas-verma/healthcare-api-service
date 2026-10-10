package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.dto.UserDto;
import com.shrinivas.healthcare_api_service.exception.OrganizationNotFoundException;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.repository.OrganizationRepository;
import com.shrinivas.healthcare_api_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    private OrganizationRepository orgRepo;


    public UserService(UserRepository userRepository,OrganizationRepository orgRepo,PasswordEncoder encoder){
        this.userRepository=userRepository;
        this.orgRepo=orgRepo;
        this.encoder=encoder;
    }

    public AppUser saveUser(UserDto userDto) throws OrganizationNotFoundException {
        Organization organization=orgRepo.findByCode(userDto.getOrgCode());
        if(organization== null){
            throw new OrganizationNotFoundException("Organization not found");
        }
        AppUser appUser=new AppUser();
        appUser.setUsername(userDto.getUsername());
        appUser.setPassword(encoder.encode(userDto.getPassword()));
        appUser.setEmail(userDto.getEmail());
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setEnabled(true);
        appUser.setId(UUID.randomUUID());
        appUser.setOrganization(organization);
        appUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(appUser);
    }

}

