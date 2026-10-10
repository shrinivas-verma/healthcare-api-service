package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import com.shrinivas.healthcare_api_service.repository.UserRepository;
import com.shrinivas.healthcare_api_service.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MyUserDetailesService implements UserDetailsService {
    private final UserRepository userRepo;
    private final UserRoleRepository userRoleRepo;

    public MyUserDetailesService(UserRepository userRepo, UserRoleRepository userRoleRepo){
        this.userRepo=userRepo;
        this.userRoleRepo=userRoleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        ));
        List<UserRole> roles=userRoleRepo.findByUser(user);

        return new UserPrincipal(user,roles);
    }
}
