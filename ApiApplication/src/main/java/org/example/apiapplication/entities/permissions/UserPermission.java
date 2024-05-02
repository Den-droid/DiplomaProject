package org.example.apiapplication.entities.permissions;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.user.User;

@Entity
@Table(name = "user_permissions")
@Data
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "permission_id", referencedColumnName = "id")
    private Permission permission;
}
