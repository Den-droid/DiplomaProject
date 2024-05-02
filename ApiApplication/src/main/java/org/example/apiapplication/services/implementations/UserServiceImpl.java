package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.permissions.UserPermissionDto;
import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.entities.permissions.UserPermission;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.auth.UserWithUsernameExistsException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.exceptions.user.RoleNotFoundException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final FacultyRepository facultyRepository;
    private final ChairRepository chairRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserPermissionRepository userPermissionRepository,
                           FacultyRepository facultyRepository,
                           ChairRepository chairRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<UserPermissionDto> getUserPermissions(User user) {
        return user.getUserPermissions().stream()
                .map(x -> new UserPermissionDto(x.getId(),
                        x.getPermission().getName().name(), x.isApproved()))
                .toList();
    }

    @Override
    public void createAdmin(CreateAdminDto createAdminDto) {
        Optional<User> optionalUser = userRepository.findByUsername(createAdminDto.email());
        if (optionalUser.isPresent()) {
            throw new UserWithUsernameExistsException(createAdminDto.email());
        }

        User user = new User();
        user.setUsername(createAdminDto.email());
        user.setEmail(createAdminDto.email());
        user.setApproved(false);
        user.setInviteCode(UUID.randomUUID().toString());

        List<Role> roles = new ArrayList<>();

        if (!createAdminDto.facultyIds().isEmpty()) {
            Role role = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(UserRole.FACULTY_ADMIN.name()));
            roles.add(role);

            for (Integer facultyId : createAdminDto.facultyIds()) {
                Faculty faculty = facultyRepository.findById(facultyId)
                        .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", facultyId));
                user.getFaculties().add(faculty);
            }
        }

        if (!createAdminDto.chairIds().isEmpty()) {
            Role role = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(UserRole.CHAIR_ADMIN.name()));
            roles.add(role);

            for (Integer chairId : createAdminDto.chairIds()) {
                Chair chair = chairRepository.findById(chairId)
                        .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));
                user.getChairs().add(chair);
            }
        }

        user.setRoles(roles);

        // send email

        userRepository.save(user);
    }

    @Override
    public void editAdmin(Integer id, EditAdminDto editAdminDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        if (!editAdminDto.facultyIds().isEmpty()) {
            List<Faculty> facultyList = new ArrayList<>();
            for (Integer facultyId : editAdminDto.facultyIds()) {
                Faculty faculty = facultyRepository.findById(facultyId)
                        .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", facultyId));

                user.getFaculties().add(faculty);
                facultyList.add(faculty);
            }

            user.getFaculties().retainAll(facultyList);
        } else {
            user.getFaculties().clear();

            Role role = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(UserRole.FACULTY_ADMIN.name()));
            user.getRoles().remove(role);
        }

        if (!editAdminDto.chairIds().isEmpty()) {
            List<Chair> chairList = new ArrayList<>();
            for (Integer chairId : editAdminDto.chairIds()) {
                Chair chair = chairRepository.findById(chairId)
                        .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));

                user.getChairs().add(chair);
                chairList.add(chair);
            }

            user.getChairs().retainAll(chairList);
        } else {
            user.getChairs().clear();

            Role role = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(UserRole.CHAIR_ADMIN.name()));
            user.getRoles().remove(role);
        }

        userRepository.save(user);
    }

    @Override
    public void activateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));
        user.setActive(true);

        // send email

        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));
        user.setActive(false);

        // send email

        userRepository.save(user);
    }

    @Override
    public void approveUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));
        user.setApproved(true);
        user.setActive(true);

        // send email

        userRepository.save(user);
    }

    @Override
    public void rejectUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        // send email

        userRepository.delete(user);
    }

    @Override
    public void askPermission(Integer userId, Integer permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Permission", permissionId));

        UserPermission userPermission = new UserPermission();
        userPermission.setPermission(permission);
        userPermission.setUser(user);
        userPermission.setApproved(false);

        userPermissionRepository.save(userPermission);
    }

    @Override
    public void approvePermission(Integer userId, Integer permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Permission", permissionId));

        UserPermission userPermission = userPermissionRepository
                .findByUserAndPermission(user, permission)
                .orElseThrow();

        userPermission.setApproved(true);
    }

    @Override
    public void rejectPermission(Integer userId, Integer permissionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Permission", permissionId));

        UserPermission userPermission = userPermissionRepository
                .findByUserAndPermission(user, permission)
                .orElseThrow();

        userPermissionRepository.delete(userPermission);
    }
}
