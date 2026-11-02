package com.shrinivas.healthcare_api_service.model.auth;

import com.shrinivas.healthcare_api_service.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="organization"
//        ,uniqueConstraints = {
//                @UniqueConstraint(name = "uk_organization_code", columnNames = "code")
//        } optional maintained bia flyway
        )
@Getter
@Setter
@NoArgsConstructor
public class Organization extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false,length = 100)
    private String code;

//    allows deactivating an organization without deleting all its users/data
    @Column(nullable = false)
    private Boolean enabled=true;
}
























