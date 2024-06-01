package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.user.*;

import java.util.List;

public interface UserService {
    GetUsersDto getForCurrentUser(Integer page);

    GetUsersDto searchForCurrentUser(String fullName, Integer roleId,
                                     Integer facultyId, Integer chairId,
                                     Integer page);

    UserDto getCurrentUser();

    List<PermissionDto> getCurrentUserPermissions();

    List<PermissionDto> getUserPermissionsById(Integer id);

    List<RoleDto> getUserRoles(Integer userId);

    UpdateAdminDto getEditDto(Integer userId);

    boolean currentUserCanEditProfile(Integer editProfileId);

    boolean currentUserCanEditUser(Integer editUserId);

    void createAdmin(CreateAdminDto createAdminDto);

    void updateAdmin(Integer id, UpdateAdminDto updateAdminDto);

    void updateUser(Integer id, UpdateUserDto updateUserDto);

    void updateCurrentUser(UpdateCurrentUserDto editUserDto);

    void activate(Integer userId);

    void deactivate(Integer userId);

    void approve(Integer id);

    void reject(Integer id);
}
