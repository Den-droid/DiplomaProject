package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.dto.user.CreateAdminDto;
import org.example.apiapplication.dto.user.EditAdminDto;
import org.example.apiapplication.dto.user.GetUsersDto;
import org.example.apiapplication.dto.user.UserDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.auth.UserWithUsernameExistsException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.exceptions.user.RoleNotFoundException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final ChairRepository chairRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ScientistRepository scientistRepository;

    public UserServiceImpl(UserRepository userRepository,
                           FacultyRepository facultyRepository,
                           ChairRepository chairRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           ScientistRepository scientistRepository) {
        this.userRepository = userRepository;
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.scientistRepository = scientistRepository;
    }

    @Override
    public GetUsersDto searchUsersByUser(User user, String fullName, Integer roleId, Integer facultyId, Integer chairId, Integer page) {
        List<User> users = getUsersByUser(user);

        if (fullName != null && !fullName.isEmpty()) {
            users = filterByName(users, fullName);
        }
        if (roleId != 0) {
            if (facultyId != 0 && chairId == 0)
                users = filterByRoleAndFaculty(users, facultyId, roleId);
            else if (facultyId == 0 && chairId != 0)
                users = filterByRoleAndChair(users, chairId, roleId);
            else
                users = filterByRole(users, roleId);
        }

        return getUserPageByListAndPage(users, page);
    }

    @Override
    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.isApproved(), user.isActive());
    }

    @Override
    public List<String> getUserRoles(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));

        return user.getRoles().stream()
                .map(x -> x.getName().name())
                .toList();
    }

    @Override
    public EditAdminDto getEditDto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));
        List<Integer> facultyIds = user.getFaculties().stream()
                .map(Faculty::getId)
                .toList();

        List<Integer> chairIds = user.getChairs().stream()
                .map(Chair::getId)
                .toList();

        return new EditAdminDto(user.getFullName(), facultyIds, chairIds);
    }

    @Override
    public GetUsersDto getUsersByUser(User user, Integer page) {
        List<User> users = getUsersByUser(user);
        return getUserPageByListAndPage(users, page);
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
        user.setActive(false);
        user.setInviteCode(UUID.randomUUID().toString());


        if (!createAdminDto.isMainAdmin()) {
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
        } else {
            Role role = roleRepository.findByName(UserRole.MAIN_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(UserRole.MAIN_ADMIN.name()));
            user.setRoles(List.of(role));
        }

        // send email

        userRepository.save(user);
    }

    @Override
    public void editAdmin(Integer id, EditAdminDto editAdminDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        user.setFullName(editAdminDto.fullName());

        Role facultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.FACULTY_ADMIN.name()));
        Role chairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.CHAIR_ADMIN.name()));

        if (!editAdminDto.facultyIds().isEmpty()) {
            if (!user.getRoles().contains(facultyAdmin)) {
                user.getRoles().add(facultyAdmin);
            }

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
            user.getRoles().remove(facultyAdmin);
        }

        if (!editAdminDto.chairIds().isEmpty()) {
            if (!user.getRoles().contains(chairAdmin)) {
                user.getRoles().add(chairAdmin);
            }
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
            user.getRoles().remove(chairAdmin);
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

        user.setRoles(new ArrayList<>());

        Scientist scientist = user.getScientists().get(0);
        scientist.setUser(null);

        userRepository.delete(user);
    }

    private List<User> filterByName(List<User> users, String name) {
        return users.stream()
                .filter(user -> user.getFullName().toLowerCase().trim()
                        .contains(name.toLowerCase().trim()))
                .toList();
    }

    private List<User> filterByRole(List<User> users, Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", roleId));

        return users.stream()
                .filter(user -> user.getRoles().contains(role))
                .toList();
    }

    private List<User> filterByRoleAndFaculty(List<User> users, Integer facultyId, Integer roleId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", facultyId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", roleId));

        Role roleFacultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN).orElseThrow();

        if (role.equals(roleFacultyAdmin)) {
            return users.stream()
                    .filter(user -> (user.getRoles().contains(roleFacultyAdmin)
                            && user.getFaculties().contains(faculty)))
                    .toList();
        } else {
            // Role User
            return users.stream()
                    .filter(user -> {
                        boolean isScientist = !user.getScientists().isEmpty();
                        boolean isUserScientistFromFaculty = isScientist &&
                                user.getScientists().get(0).getFaculty().equals(faculty);
                        boolean isUserScientistFromFacultyChair = isScientist &&
                                user.getScientists().get(0).getChair().getFaculty().equals(faculty);
                        return isUserScientistFromFaculty || isUserScientistFromFacultyChair;
                    })
                    .toList();
        }
    }

    private List<User> filterByRoleAndChair(List<User> users, Integer chairId, Integer roleId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", roleId));

        Role roleChairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (role.equals(roleChairAdmin)) {
            return users.stream()
                    .filter(user -> (user.getRoles().contains(roleChairAdmin)
                            && user.getChairs().contains(chair)))
                    .toList();
        } else {
            // Role User
            return users.stream()
                    .filter(user -> {
                        boolean isScientist = !user.getScientists().isEmpty();
                        return isScientist &&
                                user.getScientists().get(0).getChair().equals(chair);
                    })
                    .toList();
        }
    }

    private List<User> getUsersByUser(User user) {
        Role roleMainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN).orElseThrow();
        Role roleUser = roleRepository.findByName(UserRole.USER).orElseThrow();
        Role roleFacultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN).orElseThrow();
        Role roleChairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (user.getRoles().contains(roleMainAdmin)) {
            List<User> users = new ArrayList<>();
            for (User userFromIterable : userRepository.findAll()) {
                if (userFromIterable.getRoles().contains(roleUser) ||
                        userFromIterable.getRoles().contains(roleChairAdmin) ||
                        userFromIterable.getRoles().contains(roleFacultyAdmin)) {
                    users.add(userFromIterable);
                }
            }

            return users;
        } else if (!user.getRoles().contains(roleUser)) {
            List<User> users = new ArrayList<>();

            Set<Faculty> faculties = user.getFaculties();
            Set<Chair> chairs = user.getChairs();

            for (Faculty faculty : faculties) {
                List<User> tmpUsers = faculty.getScientists().stream()
                        .map(Scientist::getUser)
                        .filter(Objects::nonNull)
                        .toList();

                users.addAll(tmpUsers);
            }

            for (Chair chair : chairs) {
                List<User> tmpUsers = chair.getScientists().stream()
                        .map(Scientist::getUser)
                        .filter(Objects::nonNull)
                        .toList();

                users.addAll(tmpUsers);
            }

            return users;
        } else {
            return new ArrayList<>();
        }
    }

    private GetUsersDto getUserPageByListAndPage(List<User> users, Integer page) {
        int totalPages = (users.size() % 25) == 0 ? users.size() / 25 : users.size() / 25 + 1;

        users = users.stream()
                .skip((page - 1) * 25L)
                .limit(25)
                .toList();

        List<UserDto> userDtos = users.stream()
                .map(x -> new UserDto(x.getId(), x.getEmail(), x.getFullName(),
                        x.isApproved(), x.isActive()))
                .toList();

        return new GetUsersDto(userDtos, new PageDto(page, totalPages));
    }
}
