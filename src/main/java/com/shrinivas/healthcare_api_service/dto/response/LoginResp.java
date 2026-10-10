package com.shrinivas.healthcare_api_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LoginResp {
    private String username;
    private String jwt;
    private String email;
    private String orgCode;
    private String orgName;
    private List<LoginRespRole> loginRespRole;
}
