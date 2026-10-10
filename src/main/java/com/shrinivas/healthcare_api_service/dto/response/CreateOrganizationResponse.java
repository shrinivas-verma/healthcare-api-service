package com.shrinivas.healthcare_api_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrganizationResponse {

    private UUID organizationId;
    private String organizationCode;
    private String organizationName;

    private String adminUsername;
    private String adminEmail;

}