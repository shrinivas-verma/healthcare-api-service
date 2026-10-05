package com.shrinivas.healthcare_api_service.model.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;
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
public class AppUser {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(length = 100,nullable = false)
        private String username;

        @Column(nullable = false,length = 255)
        private String email;

        @Column(nullable = false)
        private Boolean enabled=true;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "organization_id", nullable = false)
        private Organization organization;

        @Column(name="created_at",nullable = false)
        private LocalDateTime createdAt;

        @Column(name="updated_at",nullable = false)
        private LocalDateTime updatedAt;
}




















