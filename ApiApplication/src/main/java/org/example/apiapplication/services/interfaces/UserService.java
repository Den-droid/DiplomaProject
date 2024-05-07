package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.user.*;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface UserService {
    GetUsersDto getUsersByUser(User user, Integer page);

    GetUsersDto searchUsersByUser(User user, String fullName, Integer roleId, Integer facultyId, Integer chairId, Integer page);

    UserDto getById(Integer id);

    List<PermissionDto> getUserPermissions(User user);

    List<PermissionDto> getUserPermissionsById(Integer id);

    List<RoleDto> getUserRoles(Integer userId);

    EditAdminDto getEditDto(Integer userId);

    boolean existsById(Integer id);

    void createAdmin(CreateAdminDto createAdminDto);

    void editAdmin(Integer id, EditAdminDto editAdminDto);

    void editUser(Integer id, EditUserDto editUserDto);

    void activateUser(Integer userId);

    void deactivateUser(Integer userId);

    void approveUser(Integer id);

    void rejectUser(Integer id);
}
