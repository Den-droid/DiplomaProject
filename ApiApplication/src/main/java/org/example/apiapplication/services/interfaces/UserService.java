package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.dto.user.GetUsersDto;
import org.example.apiapplication.dto.user.UserDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface UserService {
    GetUsersDto getUsersByUser(User user, Integer page);

    GetUsersDto searchUsersByUser(User user, String fullName, Integer roleId, Integer facultyId, Integer chairId, Integer page);

    boolean existsById(Integer id);

    UserDto getById(Integer id);

    List<String> getUserRoles(Integer userId);

    EditAdminDto getEditDto(Integer userId);

    void createAdmin(CreateAdminDto createAdminDto);

    void editAdmin(Integer id, EditAdminDto editAdminDto);

    void activateUser(Integer userId);

    void deactivateUser(Integer userId);

    void approveUser(Integer id);

    void rejectUser(Integer id);
}
