package com.shrinivas.healthcare_api_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class OrgDto {
    private String name;
    private String code;
    private String email;
}
