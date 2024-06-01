package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.user.*;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.permissions.Permission;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.auth.UserWithUsernameExistsException;
import org.example.apiapplication.exceptions.entity.EntityNotFoundException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.EmailService;
import org.example.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final ChairRepository chairRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    private final SessionUtil sessionUtil;

    public UserServiceImpl(UserRepository userRepository,
                           FacultyRepository facultyRepository,
                           ChairRepository chairRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           ProfileRepository profileRepository,
                           EmailService emailService, SessionUtil sessionUtil) {
        this.userRepository = userRepository;
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.profileRepository = profileRepository;
        this.emailService = emailService;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public GetUsersDto searchForCurrentUser(String fullName, Integer roleId, Integer facultyId, Integer chairId, Integer page) {
        User user = sessionUtil.getUserFromSession();

        List<User> users = getUsersByUser(user);

        if (fullName != null && !fullName.isEmpty()) {
            users = filterByName(users, fullName);
        }
        if (roleId != 0) {
            if (facultyId != 0 && chairId != 0)
                users = filterByRoleAndChair(users, chairId, roleId);
            else if (facultyId != 0)
                users = filterByRoleAndFaculty(users, facultyId, roleId);
            else
                users = filterByRole(users, roleId);
        }

        return getUserPageByListAndPage(users, page);
    }

    @Override
    public UserDto getCurrentUser() {
        User user = sessionUtil.getUserFromSession();
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(),
                user.isApproved(), user.isActive(), user.isSignedUp());
    }

    @Override
    public List<PermissionDto> getCurrentUserPermissions() {
        User user = sessionUtil.getUserFromSession();

        return user.getPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public List<PermissionDto> getUserPermissionsById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, id));

        return user.getPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public List<RoleDto> getUserRoles(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, userId));

        return user.getRoles().stream()
                .map(x -> new RoleDto(x.getId(), x.getName().name()))
                .toList();
    }

    @Override
    public UpdateAdminDto getEditDto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, userId));
        List<Integer> facultyIds = user.getFaculties().stream()
                .map(Faculty::getId)
                .toList();

        List<Integer> chairIds = user.getChairs().stream()
                .map(Chair::getId)
                .toList();

        List<Integer> permissionsIds = user.getPermissions().stream()
                .map(Permission::getId)
                .toList();

        return new UpdateAdminDto(user.getFullName(), facultyIds, chairIds, permissionsIds);
    }

    @Override
    public boolean currentUserCanEditProfile(Integer editProfileId) {
        User user = sessionUtil.getUserFromSession();

        Profile profile = profileRepository.findById(editProfileId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.PROFILE, editProfileId));

        Role mainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.MAIN_ADMIN.name()));
        Role roleUser = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.USER.name()));

        if (user.getRoles().contains(mainAdmin)) {
            return true;
        } else if (user.getRoles().contains(roleUser)) {
            List<Profile> profiles = user.getScientists().get(0).getProfiles();
            return profiles.contains(profile);
        } else {
            Scientist scientist = profile.getScientist();
            return facultyChairAdminCanChangeScientist(user, scientist);
        }
    }

    @Override
    public boolean currentUserCanEditUser(Integer editUserId) {
        User user = sessionUtil.getUserFromSession();

        User editUser = userRepository.findById(editUserId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, editUserId));

        Role mainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.MAIN_ADMIN.name()));

        if (user.getRoles().contains(mainAdmin)) {
            return !editUser.getRoles().contains(mainAdmin);
        } else {
            Role roleUser = roleRepository.findByName(UserRole.USER)
                    .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.USER.name()));

            if (editUser.getRoles().contains(roleUser)) {
                Scientist scientist = editUser.getScientists().get(0);

                return facultyChairAdminCanChangeScientist(user, scientist);
            } else {
                return false;
            }
        }
    }

    @Override
    public GetUsersDto getForCurrentUser(Integer page) {
        User user = sessionUtil.getUserFromSession();
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
        user.setSignedUp(false);
        user.setInviteCode(UUID.randomUUID().toString());

        if (!createAdminDto.isMainAdmin()) {
            List<Role> roles = new ArrayList<>();

            if (!createAdminDto.facultyIds().isEmpty()) {
                Role role = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                        .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.FACULTY_ADMIN.name()));
                roles.add(role);

                for (Integer facultyId : createAdminDto.facultyIds()) {
                    Faculty faculty = facultyRepository.findById(facultyId)
                            .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, facultyId));
                    user.getFaculties().add(faculty);
                }
            }

            if (!createAdminDto.chairIds().isEmpty()) {
                Role role = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                        .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.CHAIR_ADMIN.name()));
                roles.add(role);

                for (Integer chairId : createAdminDto.chairIds()) {
                    Chair chair = chairRepository.findById(chairId)
                            .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.CHAIR, chairId));
                    user.getChairs().add(chair);
                }
            }

            user.setRoles(roles);
        } else {
            Role role = roleRepository.findByName(UserRole.MAIN_ADMIN)
                    .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.MAIN_ADMIN.name()));
            user.setRoles(List.of(role));
        }

        List<Integer> permissionIds = createAdminDto.permissions();
        Set<Permission> permissions = permissionIds.stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.PERMISSION, x)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        emailService.signUpWithCode(user.getEmail(), user.getInviteCode());

        userRepository.save(user);
    }

    @Override
    public void updateAdmin(Integer id, UpdateAdminDto updateAdminDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, id));

        user.setFullName(updateAdminDto.fullName());

        Role facultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.FACULTY_ADMIN.name()));
        Role chairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ROLE, UserRole.CHAIR_ADMIN.name()));

        if (!updateAdminDto.facultyIds().isEmpty()) {
            if (!user.getRoles().contains(facultyAdmin)) {
                user.getRoles().add(facultyAdmin);
            }

            List<Faculty> facultyList = new ArrayList<>();
            for (Integer facultyId : updateAdminDto.facultyIds()) {
                Faculty faculty = facultyRepository.findById(facultyId)
                        .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, facultyId));

                user.getFaculties().add(faculty);
                facultyList.add(faculty);
            }

            user.getFaculties().retainAll(facultyList);
        } else {
            user.getFaculties().clear();
            user.getRoles().remove(facultyAdmin);
        }

        if (!updateAdminDto.chairIds().isEmpty()) {
            if (!user.getRoles().contains(chairAdmin)) {
                user.getRoles().add(chairAdmin);
            }
            List<Chair> chairList = new ArrayList<>();
            for (Integer chairId : updateAdminDto.chairIds()) {
                Chair chair = chairRepository.findById(chairId)
                        .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.CHAIR, chairId));

                user.getChairs().add(chair);
                chairList.add(chair);
            }

            user.getChairs().retainAll(chairList);
        } else {
            user.getChairs().clear();
            user.getRoles().remove(chairAdmin);
        }

        List<Integer> permissionIds = updateAdminDto.permissions();
        Set<Permission> permissions = permissionIds.stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.PERMISSION, x)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        userRepository.save(user);
    }

    @Override
    public void updateUser(Integer id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, id));

        Set<Permission> permissions = updateUserDto.permissionIds().stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.PERMISSION, x)))
                .collect(Collectors.toSet());

        user.setFullName(updateUserDto.fullName());
        user.setPermissions(permissions);

        userRepository.save(user);
    }

    @Override
    public void updateCurrentUser(UpdateCurrentUserDto editUserDto) {
        User user = sessionUtil.getUserFromSession();

        user.setFullName(editUserDto.fullName());

        userRepository.save(user);
    }

    @Override
    public void activate(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, userId));
        user.setActive(true);

        emailService.activateUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void deactivate(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, userId));
        user.setActive(false);

        emailService.deactivateUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void approve(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, id));
        user.setApproved(true);
        user.setActive(true);

        emailService.approveUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void reject(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER, id));

        user.setRoles(new ArrayList<>());

        Scientist scientist = user.getScientists().get(0);
        scientist.setUser(null);

        emailService.rejectUser(user.getEmail());

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
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, roleId));

        return users.stream()
                .filter(user -> user.getRoles().contains(role))
                .toList();
    }

    private List<User> filterByRoleAndFaculty(List<User> users, Integer facultyId, Integer roleId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, facultyId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, roleId));

        Role roleFacultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN).orElseThrow();
        Role roleChairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (role.equals(roleFacultyAdmin)) {
            return users.stream()
                    .filter(user -> (user.getRoles().contains(roleFacultyAdmin)
                            && user.getFaculties().contains(faculty)))
                    .toList();
        } else if (role.equals(roleChairAdmin)) {
            return users.stream()
                    .filter(user -> {
                        boolean containsChairWithinFaculty = user.getChairs().stream()
                                .anyMatch(x -> x.getFaculty().equals(faculty));
                        return user.getRoles().contains(roleChairAdmin) && containsChairWithinFaculty;
                    })
                    .toList();
        } else {
            // Role User
            return users.stream()
                    .filter(user -> {
                        boolean isScientist = !user.getScientists().isEmpty();
                        if (!isScientist)
                            return false;

                        Scientist scientist = user.getScientists().get(0);

                        Faculty userFaculty = scientist.getFaculty();
                        boolean isUserScientistFromFaculty = userFaculty != null &&
                                userFaculty.equals(faculty);
                        if (isUserScientistFromFaculty)
                            return true;

                        Faculty userFacultyChair = scientist.getChair().getFaculty();
                        boolean isUserScientistFromFacultyChair = userFacultyChair != null &&
                                userFacultyChair.equals(faculty);
                        if (isUserScientistFromFacultyChair)
                            return true;

                        return false;
                    })
                    .toList();
        }
    }

    private List<User> filterByRoleAndChair(List<User> users, Integer chairId, Integer roleId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.CHAIR, chairId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, roleId));

        Role roleChairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (role.equals(roleChairAdmin)) {
            return users.stream()
                    .filter(user -> {
                        boolean containsChair = user.getChairs().contains(chair);
                        return user.getRoles().contains(roleChairAdmin) && containsChair;
                    })
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

        if (user.getRoles().contains(roleMainAdmin)) {
            List<User> users = new ArrayList<>();
            for (User userFromIterable : userRepository.findAll()) {
                if (!userFromIterable.getRoles().contains(roleMainAdmin)) {
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
                        x.isApproved(), x.isActive(), x.isSignedUp()))
                .toList();

        return new GetUsersDto(userDtos, new PageDto(page, totalPages));
    }

    private boolean facultyChairAdminCanChangeScientist(User user, Scientist scientist) {
        Faculty faculty = scientist.getFaculty();
        Chair chair = scientist.getChair();

        Set<Faculty> faculties = user.getFaculties();
        Set<Chair> chairs = user.getChairs();

        if (faculty != null) {
            if (faculties != null && !faculties.isEmpty()) {
                return faculties.contains(faculty);
            } else {
                return false;
            }
        } else if (chair != null) {
            if (chairs != null && !chairs.isEmpty()) {
                return chairs.contains(chair);
            }

            if (faculties != null && !faculties.isEmpty()) {
                for (Faculty userFaculty : faculties) {
                    if (userFaculty.equals(chair.getFaculty())) {
                        return userFaculty.getChairs().contains(chair);
                    }
                }
                return false;
            }
        }

        return false;
    }
}
