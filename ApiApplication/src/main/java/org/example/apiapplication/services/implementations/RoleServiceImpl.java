package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.roles.UpdateDefaultPermissionsDto;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityNotFoundException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.PermissionRepository;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.services.interfaces.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<RoleDto> getRoles() {
        List<Role> roles = new ArrayList<>();
        for (Role role : roleRepository.findAll()) {
            roles.add(role);
        }

        return roles.stream()
                .map(x -> new RoleDto(x.getId(), x.getName().name()))
                .toList();
    }

    @Override
    public List<PermissionDto> getPossiblePermissions(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", id));

        return role.getPossiblePermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public List<PermissionDto> getDefaultPermissions(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", id));

        return role.getDefaultPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public RoleDto getByName(String roleName) {
        Role role = roleRepository.findByName(UserRole.valueOf(roleName))
                .orElseThrow(() -> new EntityNotFoundException("Role", roleName));

        return new RoleDto(role.getId(), role.getName().name());
    }

    @Override
    public void updateDefaultPermissions(List<UpdateDefaultPermissionsDto> defaultPermissionsDtos) {
        for (UpdateDefaultPermissionsDto updateDefaultPermissionsDto : defaultPermissionsDtos) {
            Role role = roleRepository.findById(updateDefaultPermissionsDto.roleId())
                    .orElseThrow(() -> new EntityWithIdNotExistsException("Role",
                            updateDefaultPermissionsDto.roleId()));

            Set<Permission> newPermissions = updateDefaultPermissionsDto.defaultPermissionsIds()
                    .stream()
                    .map(x -> permissionRepository.findById(x)
                            .orElseThrow(() -> new EntityWithIdNotExistsException("Permission", x)))
                    .collect(Collectors.toSet());

            role.setDefaultPermissions(newPermissions);
        }
    }
}
