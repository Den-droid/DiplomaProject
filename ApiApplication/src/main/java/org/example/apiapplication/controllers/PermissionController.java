package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.services.interfaces.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        List<PermissionDto> permissionDtos = permissionService.getAll();
        return ResponseEntity.ok(permissionDtos);
    }
}
