package com.shrinivas.healthcare_api_service.dto.response;

import com.shrinivas.healthcare_api_service.model.auth.Organization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CreateUserResp {
    private UUID id;
    private String username;
    private String email;
    private Organization organization;
    private LocalDateTime createdAt;

}
