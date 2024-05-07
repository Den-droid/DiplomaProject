package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.UpdateDefaultPermissionsDto;
import org.example.apiapplication.services.interfaces.RoleService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> getAllRoles(@RequestParam(required = false) String roleName) {
        if (roleName != null) {
            return ResponseEntity.ok(roleService.getByName(roleName));
        } else {
            return ResponseEntity.ok(roleService.getRoles());
        }
    }

    @GetMapping("/{id}/possiblePermissions")
    public ResponseEntity<?> getPossiblePermissions(@PathVariable Integer id) {
        List<PermissionDto> permissions = roleService.getPossiblePermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}/defaultPermissions")
    public ResponseEntity<?> getDefaultPermissions(@PathVariable Integer id) {
        List<PermissionDto> permissions = roleService.getDefaultPermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/updateDefaultPermissions")
    public ResponseEntity<?> updateDefaultPermissions(
            @RequestBody List<UpdateDefaultPermissionsDto> updateDefaultPermissionsDtos) {
        roleService.updateDefaultPermissions(updateDefaultPermissionsDtos);
        return ResponseEntity.ok().build();
    }
}
