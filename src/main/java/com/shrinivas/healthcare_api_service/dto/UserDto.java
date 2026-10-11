package com.shrinivas.healthcare_api_service.dto;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private RoleName roleName;
}
