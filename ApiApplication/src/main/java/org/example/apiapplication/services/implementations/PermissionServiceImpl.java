package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.repositories.PermissionRepository;
import org.example.apiapplication.services.interfaces.PermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<PermissionDto> getAll() {
        List<Permission> permissions = new ArrayList<>();
        for (Permission permission : permissionRepository.findAll()) {
            permissions.add(permission);
        }

        return permissions.stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }
}
