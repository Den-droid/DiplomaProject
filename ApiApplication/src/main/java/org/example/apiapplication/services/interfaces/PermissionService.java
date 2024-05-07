package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.permissions.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<PermissionDto> getAll();
}
