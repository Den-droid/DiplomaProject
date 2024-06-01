package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.user.*;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/accessible-for-current-user")
    public ResponseEntity<?> getForCurrentUser(@RequestParam Integer currentPage) {
        GetUsersDto getUsersDto = userService.getForCurrentUser(currentPage);
        return ResponseEntity.ok(getUsersDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/accessible-for-current-user/search")
    public ResponseEntity<?> searchForCurrentUser(@RequestParam Integer currentPage,
                                                  @RequestParam String fullName,
                                                  @RequestParam Integer roleId,
                                                  @RequestParam Integer facultyId,
                                                  @RequestParam Integer chairId) {
        GetUsersDto getUsersDto = userService.searchForCurrentUser(fullName, roleId,
                facultyId, chairId, currentPage);
        return ResponseEntity.ok(getUsersDto);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminDto createAdminDto) {
        userService.createAdmin(createAdminDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/edit-dto")
    public ResponseEntity<?> getEditDto(@PathVariable Integer id) {
        UpdateAdminDto updateAdminDto = userService.getEditDto(id);
        return ResponseEntity.ok(updateAdminDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Integer id) {
        List<RoleDto> roles = userService.getUserRoles(id);
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current-user/permissions")
    public ResponseEntity<?> getCurrentUserPermissions() {
        List<PermissionDto> permissions = userService.getCurrentUserPermissions();
        return ResponseEntity.ok(permissions);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/permissions")
    public ResponseEntity<?> getPermissions(@PathVariable Integer id) {
        List<PermissionDto> permissions = userService.getUserPermissionsById(id);
        return ResponseEntity.ok(permissions);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/admins/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id,
                                         @RequestBody UpdateAdminDto updateAdminDto) {
        userService.updateAdmin(id, updateAdminDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @RequestBody UpdateUserDto updateUserDto) {
        userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Integer id) {
        userService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Integer id) {
        userService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Integer id) {
        userService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Integer id) {
        userService.reject(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PutMapping("/current-user")
    public ResponseEntity<?> editCurrentUser(@RequestBody UpdateCurrentUserDto editUserDto) {
        userService.updateCurrentUser(editUserDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        UserDto userDto = userService.getCurrentUser();
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/current-user/can-update-user")
    public ResponseEntity<?> canUpdateUser(@RequestParam Integer userId) {
        boolean canEdit = userService.currentUserCanEditUser(userId);
        return ResponseEntity.ok(canEdit);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current-user/can-update-profile")
    public ResponseEntity<?> canUpdateProfile(@RequestParam Integer profileId) {
        boolean canEdit = userService.currentUserCanEditProfile(profileId);
        return ResponseEntity.ok(canEdit);
    }
}
