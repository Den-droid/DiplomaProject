package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.permissions.UserPermissionDto;
import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface UserService {
    List<UserPermissionDto> getUserPermissions(User user);

    void createAdmin(CreateAdminDto createAdminDto);

    void editAdmin(Integer id, EditAdminDto editAdminDto);

    void activateUser(Integer userId);

    void deactivateUser(Integer userId);

    void approveUser(Integer id);

    void rejectUser(Integer id);

    void askPermission(Integer userId, Integer permissionId);

    void approvePermission(Integer userId, Integer permissionId);

    void rejectPermission(Integer userId, Integer permissionId);
}
