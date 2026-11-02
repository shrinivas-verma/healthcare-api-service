package com.shrinivas.healthcare_api_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CreateUserResp {
    private UUID id;
    private String username;
    private String email;
    private OrganizationResp organization;
    private Instant createdAt;

}
