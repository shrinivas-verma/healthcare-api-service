package com.shrinivas.healthcare_api_service.model.auth;

import com.shrinivas.healthcare_api_service.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="role"
//        ,uniqueConstraints = {
//                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
//        }
        )
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 100)
    private RoleName name;

    @Column(length = 500)
    private String description;
}
