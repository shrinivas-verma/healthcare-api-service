package com.shrinivas.healthcare_api_service.model.auth;

import com.shrinivas.healthcare_api_service.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="app_user"
//        ,uniqueConstraints = {
//        @UniqueConstraint(name="uk_app_user_username",columnNames = "username"),
//        @UniqueConstraint(name="uk_app_user_email",columnNames = "email")
//        }
        )
@NoArgsConstructor
@Getter
@Setter
public class AppUser extends BaseAuditableEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(length = 100,nullable = false)
        private String username;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false,length = 255)
        private String email;

        @Column(nullable = false)
        private Boolean enabled=true;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "organization_id", nullable = false)
        private Organization organization;
}




















