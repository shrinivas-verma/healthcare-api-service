package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.dto.LoginDto;
import com.shrinivas.healthcare_api_service.dto.UserDto;
import com.shrinivas.healthcare_api_service.dto.response.CreateUserResp;
import com.shrinivas.healthcare_api_service.dto.response.LoginResp;
import com.shrinivas.healthcare_api_service.dto.response.LoginRespRole;
import com.shrinivas.healthcare_api_service.enums.RoleName;
import com.shrinivas.healthcare_api_service.exception.OrganizationNotFoundException;
import com.shrinivas.healthcare_api_service.exception.UserCreationNotAllowedException;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.model.auth.Role;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import com.shrinivas.healthcare_api_service.repository.RoleRepository;
import com.shrinivas.healthcare_api_service.repository.UserRepository;
import com.shrinivas.healthcare_api_service.repository.UserRoleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private UserRoleRepository userRoleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder encoder,
                       RoleRepository roleRepository, JwtService jwtService,
                       AuthenticationManager authenticationManager, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRoleRepository=userRoleRepository;
    }

    @Transactional
    public CreateUserResp registerUser(UserDto userDto) throws OrganizationNotFoundException {
        if (userDto.getRoleName() == RoleName.SYSTEM_ADMIN) {
            throw new UserCreationNotAllowedException("User Creation Now Allowed");
        }
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {

            throw new AccessDeniedException("Authenticated user not found");
        }

        String adminUsername = authentication.getName();

        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        Organization organization = admin.getOrganization();

        if (!organization.getEnabled()) {
            throw new AccessDeniedException("Your organization is disabled");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        RoleName requestedRole = userDto.getRoleName();

        if (requestedRole != RoleName.FILE_OPERATOR
                && requestedRole != RoleName.REPORT_VIEWER
                && requestedRole != RoleName.AUDITOR) {
            throw new AccessDeniedException("You cannot assign this role");
        }

//        Add check if auth user belongs to org he is creating user for?

        AppUser newUser = new AppUser();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(encoder.encode(userDto.getPassword()));
        newUser.setEmail(userDto.getEmail());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setEnabled(true);
//        newUser.setId(UUID.randomUUID());
        newUser.setOrganization(organization);
        newUser.setUpdatedAt(LocalDateTime.now());

        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() ->
                        new IllegalStateException("Role not configured"));

        UserRole userRole = new UserRole();
//        userRole.setId(UUID.randomUUID());
        userRole.setUser(newUser);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        userRepository.save(newUser);

        CreateUserResp resp = new CreateUserResp();
        resp.setUsername(newUser.getUsername());
        resp.setCreatedAt(newUser.getCreatedAt());
        resp.setId(newUser.getId());
        resp.setOrganization(newUser.getOrganization());
        return resp;
    }

    public LoginResp loginUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        AppUser appUser = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        List<UserRole> roles = userRoleRepository.findByUser(appUser);

        String jwt = jwtService.generateJwt(loginDto.getUsername());

        LoginResp res = new LoginResp();
        res.setEmail(appUser.getEmail());
        res.setJwt(jwt);
        res.setOrgCode(appUser.getOrganization().getCode());
        res.setOrgName(appUser.getOrganization().getName());
//        for(UserRole role:roles){
//            System.out.println(role.getRole().getName());
//        }
        res.setLoginRespRole(roles.stream()
                .map((role) -> new LoginRespRole(role.getRole().getName(), role.getRole().getDescription())).toList()
        );
        res.setUsername(loginDto.getUsername());
        return res;


    }
}

