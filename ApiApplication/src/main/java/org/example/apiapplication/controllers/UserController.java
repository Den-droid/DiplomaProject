package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.dto.user.*;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
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
    private final SessionUtil sessionUtil;

    public UserController(UserService userService,
                          SessionUtil sessionUtil) {
        this.userService = userService;
        this.sessionUtil = sessionUtil;
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam Integer currentPage) {
        User user = sessionUtil.getUserFromSession();
        GetUsersDto getUsersDto = userService.getUsersByUser(user, currentPage);
        return ResponseEntity.ok(getUsersDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam Integer currentPage,
                                         @RequestParam String fullName,
                                         @RequestParam Integer roleId,
                                         @RequestParam Integer facultyId,
                                         @RequestParam Integer chairId) {
        User user = sessionUtil.getUserFromSession();
        GetUsersDto getUsersDto = userService.searchUsersByUser(user, fullName, roleId,
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
    @GetMapping("/{id}/editDto")
    public ResponseEntity<?> getEditDto(@PathVariable Integer id) {
        EditAdminDto editAdminDto = userService.getEditDto(id);
        return ResponseEntity.ok(editAdminDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Integer id) {
        List<RoleDto> roles = userService.getUserRoles(id);
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current/permissions")
    public ResponseEntity<?> getCurrentUserPermissions() {
        User user = sessionUtil.getUserFromSession();
        List<PermissionDto> permissions = userService.getUserPermissions(user);
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
    public ResponseEntity<?> editAdmin(@PathVariable Integer id,
                                       @RequestBody EditAdminDto editAdminDto) {
        userService.editAdmin(id, editAdminDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable Integer id,
                                      @RequestBody EditUserDto editUserDto) {
        userService.editUser(id, editUserDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Integer id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Integer id) {
        userService.approveUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Integer id) {
        userService.rejectUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PutMapping("/current")
    public ResponseEntity<?> editCurrentUser(@RequestBody EditCurrentUserDto editUserDto) {
        User user = sessionUtil.getUserFromSession();
        userService.editCurrentUser(user, editUserDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCurrentUser(@PathVariable Integer id) {
        UserDto userDto = userService.getById(id);
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN')")
    @GetMapping("/current/canEditUser")
    public ResponseEntity<?> canEditUser(@RequestParam Integer userId) {
        User user = sessionUtil.getUserFromSession();
        boolean canEdit = userService.canEditUser(user, userId);
        return ResponseEntity.ok(canEdit);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current/canEditProfile")
    public ResponseEntity<?> canEditProfile(@RequestParam Integer profileId) {
        User user = sessionUtil.getUserFromSession();
        boolean canEdit = userService.canEditProfile(user, profileId);
        return ResponseEntity.ok(canEdit);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current/faculties")
    public ResponseEntity<?> getUserFaculties() {
        User user = sessionUtil.getUserFromSession();
        List<FacultyDto> faculties = userService.getUserFaculties(user);
        return ResponseEntity.ok(faculties);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current/chairs")
    public ResponseEntity<?> getUserChairs() {
        User user = sessionUtil.getUserFromSession();
        List<ChairDto> chairs = userService.getUserChairs(user);
        return ResponseEntity.ok(chairs);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/current/scientists")
    public ResponseEntity<?> getUserScientists() {
        User user = sessionUtil.getUserFromSession();
        List<ScientistPreviewDto> scientists = userService.getUserScientists(user);
        return ResponseEntity.ok(scientists);
    }
}
