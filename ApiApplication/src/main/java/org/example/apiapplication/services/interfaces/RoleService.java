package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.roles.UpdateDefaultPermissionsDto;

import java.util.List;

public interface RoleService {
    List<RoleDto> getAll();

    List<PermissionDto> getPossiblePermissions(Integer id);

    List<PermissionDto> getDefaultPermissions(Integer id);

    void updateDefaultPermissions(List<UpdateDefaultPermissionsDto> defaultPermissionsDtos);
}
