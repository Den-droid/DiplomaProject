package org.example.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.permissions.Permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "forgot_password_token")
    private String forgotPasswordToken;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "is_approved")
    private boolean isApproved;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_signed_up")
    private boolean isSignedUp;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_faculties",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "faculty_id"))
    private Set<Faculty> faculties = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_chairs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chair_id"))
    private Set<Chair> chairs = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    // actually it is one to one relation but due to the system features it is one to many
    @OneToMany(mappedBy = "user")
    private List<Scientist> scientists = new ArrayList<>();
}
