package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.roles.UpdateDefaultPermissionsDto;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
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
    public List<RoleDto> getAll() {
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
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, id));

        return role.getPossiblePermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public List<PermissionDto> getDefaultPermissions(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, id));

        return role.getDefaultPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public void updateDefaultPermissions(List<UpdateDefaultPermissionsDto> defaultPermissionsDtos) {
        for (UpdateDefaultPermissionsDto updateDefaultPermissionsDto : defaultPermissionsDtos) {
            Role role = roleRepository.findById(updateDefaultPermissionsDto.roleId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE,
                            updateDefaultPermissionsDto.roleId()));

            Set<Permission> newPermissions = updateDefaultPermissionsDto.defaultPermissionsIds()
                    .stream()
                    .map(x -> permissionRepository.findById(x)
                            .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.PERMISSION, x)))
                    .collect(Collectors.toSet());

            role.setDefaultPermissions(newPermissions);
        }
    }
}
