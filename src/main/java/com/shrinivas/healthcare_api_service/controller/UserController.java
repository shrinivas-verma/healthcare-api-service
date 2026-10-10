package com.shrinivas.healthcare_api_service.controller;

import com.shrinivas.healthcare_api_service.dto.OrgDto;
import com.shrinivas.healthcare_api_service.dto.UserDto;
import com.shrinivas.healthcare_api_service.dto.response.CreateOrganizationResponse;
import com.shrinivas.healthcare_api_service.exception.OrganizationNotFoundException;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.service.OrganizationService;
import com.shrinivas.healthcare_api_service.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;
    private final OrganizationService orgService;

    public UserController(UserService userService, OrganizationService orgService) {
        this.userService = userService;
        this.orgService=orgService;
    }

    @PreAuthorize("hasRole('ORG_ADMIN')")
    @PostMapping("register/user")
    public AppUser registerUser(@RequestBody UserDto userDto) throws OrganizationNotFoundException {
        return userService.saveUser(userDto);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("register/organization")
    public CreateOrganizationResponse registerOrganization(@RequestBody OrgDto orgDto)  {
        return orgService.createOrganization(orgDto);
    }
}
