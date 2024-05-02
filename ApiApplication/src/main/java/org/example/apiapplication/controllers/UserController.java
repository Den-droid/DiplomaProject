package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.permissions.UserPermissionDto;
import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SessionUtil sessionUtil;

    public UserController(UserService userService,
                          SessionUtil sessionUtil) {
        this.userService = userService;
        this.sessionUtil = sessionUtil;
    }

    @GetMapping("/current/permissions")
    public ResponseEntity<?> getCurrentUserPermissions() {
        User user = sessionUtil.getUserFromSession();
        List<UserPermissionDto> userPermissions = userService.getUserPermissions(user);
        return ResponseEntity.ok(userPermissions);
    }

    @PostMapping()
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminDto createAdminDto) {
        userService.createAdmin(createAdminDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> editAdmin(@PathVariable Integer id,
                                       @RequestBody EditAdminDto editAdminDto) {
        userService.editAdmin(id, editAdminDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Integer id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Integer id) {
        userService.approveUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Integer id) {
        userService.rejectUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<?> askPermission(@PathVariable Integer id,
                                           @RequestBody Integer permissionId) {
        userService.askPermission(id, permissionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/permissions/{permissionId}/approve")
    public ResponseEntity<?> approvePermission(@PathVariable Integer userId,
                                               @PathVariable Integer permissionId) {
        userService.approvePermission(userId, permissionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/permissions/{permissionId}/reject")
    public ResponseEntity<?> rejectPermission(@PathVariable Integer userId,
                                              @PathVariable Integer permissionId) {
        userService.rejectPermission(userId, permissionId);
        return ResponseEntity.ok().build();
    }
}
