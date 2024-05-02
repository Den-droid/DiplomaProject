package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.entities.permissions.UserPermission;
import org.example.apiapplication.entities.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserPermissionRepository extends CrudRepository<UserPermission, Integer> {
    Optional<UserPermission> findByUserAndPermission(User user, Permission permission);
}
