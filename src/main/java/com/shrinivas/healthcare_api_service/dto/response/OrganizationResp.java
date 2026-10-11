package com.shrinivas.healthcare_api_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class OrganizationResp {
    private UUID Id;
    private String name;
    private String code;
    private Boolean enabled=true;
}
