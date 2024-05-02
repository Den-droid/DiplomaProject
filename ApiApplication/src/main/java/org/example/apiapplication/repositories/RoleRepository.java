package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.enums.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(UserRole name);
}
