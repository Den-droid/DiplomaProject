package org.example.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.enums.UserRole;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private UserRole name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "role_possible_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> possiblePermissions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "role_default_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> defaultPermissions = new HashSet<>();
}
