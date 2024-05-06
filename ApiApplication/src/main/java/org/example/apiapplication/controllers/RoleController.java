package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.services.interfaces.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getAllRoles() {
        List<RoleDto> roleDtos = roleService.getRoles();
        return ResponseEntity.ok(roleDtos);
    }
}
