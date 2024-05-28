package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.roles.UpdateDefaultPermissionsDto;
import org.example.apiapplication.services.interfaces.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        List<RoleDto> roles = roleService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/possiblePermissions")
    public ResponseEntity<?> getPossiblePermissions(@PathVariable Integer id) {
        List<PermissionDto> permissions = roleService.getPossiblePermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/defaultPermissions")
    public ResponseEntity<?> getDefaultPermissions(@PathVariable Integer id) {
        List<PermissionDto> permissions = roleService.getDefaultPermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/updateDefaultPermissions")
    public ResponseEntity<?> updateDefaultPermissions(
            @RequestBody List<UpdateDefaultPermissionsDto> updateDefaultPermissionsDtos) {
        roleService.updateDefaultPermissions(updateDefaultPermissionsDtos);
        return ResponseEntity.ok().build();
    }
}
