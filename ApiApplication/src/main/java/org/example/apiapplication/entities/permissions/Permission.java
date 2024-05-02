package org.example.apiapplication.entities.permissions;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.enums.UserPermissionName;

@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private UserPermissionName name;
}
