package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import com.shrinivas.healthcare_api_service.model.auth.UserRole;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final AppUser user;
    private final List<UserRole> roles;

    public UserPrincipal(AppUser user, List<UserRole> roles) {
        this.user=user;
        this.roles=roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<RoleName> roleNames=roles.stream().map((role)-> role.getRole().getName()).toList();
        return roleNames.stream().map((roleName)->
                 new SimpleGrantedAuthority("ROLE_"+roleName)).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
//        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
//        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
//        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
//        return UserDetails.super.isEnabled();
    }
}
