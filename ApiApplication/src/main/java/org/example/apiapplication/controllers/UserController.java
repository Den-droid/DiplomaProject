package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.dto.user.GetUsersDto;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam Integer currentPage) {
        User user = sessionUtil.getUserFromSession();
        GetUsersDto getUsersDto = userService.getUsersByUser(user, currentPage);
        return ResponseEntity.ok(getUsersDto);
    }

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

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> existsUser(@PathVariable Integer id) {
        boolean userExists = userService.existsById(id);
        return ResponseEntity.ok(userExists);
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminDto createAdminDto) {
        userService.createAdmin(createAdminDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/editDto")
    public ResponseEntity<?> getEditDto(@PathVariable Integer id) {
        EditAdminDto editAdminDto = userService.getEditDto(id);
        return ResponseEntity.ok(editAdminDto);
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Integer id) {
        List<String> roles = userService.getUserRoles(id);
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/admins/{id}")
    public ResponseEntity<?> editAdmin(@PathVariable Integer id,
                                       @RequestBody EditAdminDto editAdminDto) {
        userService.editAdmin(id, editAdminDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Integer id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Integer id) {
        userService.approveUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Integer id) {
        userService.rejectUser(id);
        return ResponseEntity.ok().build();
    }
}
