package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.permissions.Permission;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, Integer> {
}
