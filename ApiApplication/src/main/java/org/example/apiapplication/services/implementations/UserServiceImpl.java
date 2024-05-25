package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.dto.permissions.PermissionDto;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
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
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.*;
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
    private final ScientistRepository scientistRepository;

    public UserServiceImpl(UserRepository userRepository,
                           FacultyRepository facultyRepository,
                           ChairRepository chairRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           ProfileRepository profileRepository,
                           EmailService emailService, ScientistRepository scientistRepository) {
        this.userRepository = userRepository;
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.profileRepository = profileRepository;
        this.emailService = emailService;
        this.scientistRepository = scientistRepository;
    }

    @Override
    public GetUsersDto searchUsersByUser(User user, String fullName, Integer roleId, Integer facultyId, Integer chairId, Integer page) {
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
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(),
                user.isApproved(), user.isActive(), user.isSignedUp());
    }

    @Override
    public List<PermissionDto> getUserPermissions(User user) {
        return user.getPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName().name()))
                .toList();
    }

    @Override
    public List<PermissionDto> getUserPermissionsById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        return getUserPermissions(user);
    }

    @Override
    public List<RoleDto> getUserRoles(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));

        return user.getRoles().stream()
                .map(x -> new RoleDto(x.getId(), x.getName().name()))
                .toList();
    }

    @Override
    public List<ChairDto> getUserChairs(User user) {
        Role adminRole = roleRepository.findByName(UserRole.MAIN_ADMIN).orElseThrow();
        Role facultyRole = roleRepository.findByName(UserRole.FACULTY_ADMIN).orElseThrow();
        Role chairRole = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (user.getRoles().contains(facultyRole) || user.getRoles().contains(chairRole)) {
            Set<Chair> chairSet = new HashSet<>(user.getChairs());
            for (Faculty faculty : user.getFaculties()) {
                chairSet.addAll(faculty.getChairs());
            }

            return chairSet.stream()
                    .map(x -> new ChairDto(x.getId(), x.getUkrainianName(), x.getFaculty().getId()))
                    .toList();
        } else if (user.getRoles().contains(adminRole)) {
            List<Chair> chairs = new ArrayList<>();
            for (Chair chair : chairRepository.findAll()) {
                chairs.add(chair);
            }

            return chairs.stream()
                    .map(x -> new ChairDto(x.getId(), x.getUkrainianName(),
                            x.getFaculty().getId()))
                    .toList();
        } else {
            Chair chair = user.getScientists().get(0).getChair();
            return List.of(new ChairDto(chair.getId(), chair.getUkrainianName(),
                    chair.getFaculty().getId()));
        }
    }

    @Override
    public List<FacultyDto> getUserFaculties(User user) {
        Role adminRole = roleRepository.findByName(UserRole.MAIN_ADMIN).orElseThrow();
        Role facultyRole = roleRepository.findByName(UserRole.FACULTY_ADMIN).orElseThrow();
        Role chairRole = roleRepository.findByName(UserRole.CHAIR_ADMIN).orElseThrow();

        if (user.getRoles().contains(facultyRole) || user.getRoles().contains(chairRole)) {
            Set<Faculty> facultySet = new HashSet<>(user.getFaculties());
            for (Chair chair : user.getChairs()) {
                facultySet.add(chair.getFaculty());
            }
            facultySet.addAll(user.getFaculties());

            return facultySet.stream()
                    .map(x -> new FacultyDto(x.getId(), x.getUkrainianName()))
                    .toList();
        } else if (user.getRoles().contains(adminRole)) {
            List<Faculty> faculties = new ArrayList<>();
            for (Faculty faculty : facultyRepository.findAll()) {
                faculties.add(faculty);
            }
            return faculties.stream()
                    .map(x -> new FacultyDto(x.getId(), x.getUkrainianName()))
                    .toList();
        } else {
            Scientist scientist = user.getScientists().get(0);

            Faculty faculty = scientist.getFaculty();
            if (faculty != null)
                return List.of(new FacultyDto(faculty.getId(), faculty.getUkrainianName()));
            else {
                Faculty chairFaculty = scientist.getChair().getFaculty();
                return List.of(new FacultyDto(chairFaculty.getId(), chairFaculty.getUkrainianName()));
            }
        }
    }

    @Override
    public List<ScientistPreviewDto> getUserScientists(User user) {
        String userRole = user.getRoles().get(0).getName().name();

        if (userRole.equals(UserRole.MAIN_ADMIN.name())) {
            List<Scientist> scientists = new ArrayList<>();
            for (Scientist scientist : scientistRepository.findAll()) {
                scientists.add(scientist);
            }

            return scientists.stream()
                    .map((x) -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                    .toList();
        } else if (userRole.equals(UserRole.USER.name())) {
            Scientist scientist = user.getScientists().get(0);
            return List.of(new ScientistPreviewDto(scientist.getId(), scientist.getFullName()));
        } else if (userRole.equals(UserRole.CHAIR_ADMIN.name())) {
            List<Scientist> scientists = new ArrayList<>();

            Set<Chair> chairs = user.getChairs();
            for (Chair chair : chairs) {
                scientists.addAll(chair.getScientists());
            }

            return scientists.stream()
                    .map(x -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                    .toList();
        } else {
            List<Scientist> scientists = new ArrayList<>();

            Set<Faculty> faculties = user.getFaculties();
            for (Faculty faculty : faculties) {
                scientists.addAll(faculty.getScientists());

                for (Chair chair : faculty.getChairs()) {
                    scientists.addAll(chair.getScientists());
                }
            }

            return scientists.stream()
                    .map(x -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                    .toList();
        }
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

        List<Integer> permissionsIds = user.getPermissions().stream()
                .map(Permission::getId)
                .toList();

        return new EditAdminDto(user.getFullName(), facultyIds, chairIds, permissionsIds);
    }

    @Override
    public boolean canEditProfile(User user, Integer editProfileId) {
        Profile profile = profileRepository.findById(editProfileId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", editProfileId));

        Role mainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.MAIN_ADMIN.name()));
        Role roleUser = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.USER.name()));

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
    public boolean canEditUser(User user, Integer editUserId) {
        User editUser = userRepository.findById(editUserId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", editUserId));

        Role mainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.MAIN_ADMIN.name()));

        if (user.getRoles().contains(mainAdmin)) {
            return !editUser.getRoles().contains(mainAdmin);
        } else {
            Role roleUser = roleRepository.findByName(UserRole.USER)
                    .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.USER.name()));

            if (editUser.getRoles().contains(roleUser)) {
                Scientist scientist = editUser.getScientists().get(0);

                return facultyChairAdminCanChangeScientist(user, scientist);
            } else {
                return false;
            }
        }
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
        user.setSignedUp(false);
        user.setInviteCode(UUID.randomUUID().toString());

        if (!createAdminDto.isMainAdmin()) {
            List<Role> roles = new ArrayList<>();

            if (!createAdminDto.facultyIds().isEmpty()) {
                Role role = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                        .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.FACULTY_ADMIN.name()));
                roles.add(role);

                for (Integer facultyId : createAdminDto.facultyIds()) {
                    Faculty faculty = facultyRepository.findById(facultyId)
                            .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", facultyId));
                    user.getFaculties().add(faculty);
                }
            }

            if (!createAdminDto.chairIds().isEmpty()) {
                Role role = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                        .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.CHAIR_ADMIN.name()));
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
                    .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.MAIN_ADMIN.name()));
            user.setRoles(List.of(role));
        }

        List<Integer> permissionIds = createAdminDto.permissions();
        Set<Permission> permissions = permissionIds.stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotExistsException("Permission", x)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        emailService.signUpWithCode(user.getEmail(), user.getInviteCode());

        userRepository.save(user);
    }

    @Override
    public void editAdmin(Integer id, EditAdminDto editAdminDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        user.setFullName(editAdminDto.fullName());

        Role facultyAdmin = roleRepository.findByName(UserRole.FACULTY_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.FACULTY_ADMIN.name()));
        Role chairAdmin = roleRepository.findByName(UserRole.CHAIR_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Role", UserRole.CHAIR_ADMIN.name()));

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

        List<Integer> permissionIds = editAdminDto.permissions();
        Set<Permission> permissions = permissionIds.stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotExistsException("Permission", x)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        userRepository.save(user);
    }

    @Override
    public void editUser(Integer id, EditUserDto editUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

        Set<Permission> permissions = editUserDto.permissionIds().stream()
                .map(x -> permissionRepository.findById(x).orElseThrow(() -> new EntityWithIdNotExistsException("Permission", x)))
                .collect(Collectors.toSet());

        user.setFullName(editUserDto.fullName());
        user.setPermissions(permissions);

        userRepository.save(user);
    }

    @Override
    public void editCurrentUser(User user, EditCurrentUserDto editUserDto) {
        user.setFullName(editUserDto.fullName());

        userRepository.save(user);
    }

    @Override
    public void activateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));
        user.setActive(true);

        emailService.activateUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", userId));
        user.setActive(false);

        emailService.deactivateUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void approveUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));
        user.setApproved(true);
        user.setActive(true);

        emailService.approveUser(user.getEmail());

        userRepository.save(user);
    }

    @Override
    public void rejectUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("User", id));

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
                .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Role", roleId));

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
