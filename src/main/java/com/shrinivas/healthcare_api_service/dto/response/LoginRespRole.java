package com.shrinivas.healthcare_api_service.dto.response;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginRespRole {
    private RoleName roleName;
    private String roleDesc;
}
