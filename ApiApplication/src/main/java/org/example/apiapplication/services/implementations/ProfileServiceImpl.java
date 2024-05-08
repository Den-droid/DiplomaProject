package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;
import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.dto.profile.AddProfileDto;
import org.example.apiapplication.dto.profile.EditProfileDto;
import org.example.apiapplication.dto.profile.GetProfilesDto;
import org.example.apiapplication.dto.profile.ProfilePreviewDto;
import org.example.apiapplication.entities.*;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.FieldType;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.exceptions.profile.ProfileScientistScientometricSystemExists;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.LabelService;
import org.example.apiapplication.services.interfaces.ProfileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final ProfileRepository profileRepository;
    private final ScientistRepository scientistRepository;
    private final FieldRepository fieldRepository;
    private final ProfileFieldValueRepository profileFieldValueRepository;
    private final RoleRepository roleRepository;
    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;

    private final LabelService labelService;

    public ProfileServiceImpl(ScientometricSystemRepository scientometricSystemRepository,
                              ProfileRepository profileRepository,
                              ScientistRepository scientistRepository,
                              FieldRepository fieldRepository,
                              ProfileFieldValueRepository profileFieldValueRepository,
                              LabelService labelService,
                              RoleRepository roleRepository,
                              ChairRepository chairRepository,
                              FacultyRepository facultyRepository) {
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.profileRepository = profileRepository;
        this.scientistRepository = scientistRepository;
        this.fieldRepository = fieldRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
        this.roleRepository = roleRepository;

        this.labelService = labelService;
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public GetProfilesDto getByUserAndScientometricSystemId(User user, Integer scientometricSystemId, Integer page) {
        List<Profile> profiles = getProfilesByUserAndScientometricSystem(user, scientometricSystemId);
        return getProfilePageByListAndPage(profiles, page);
    }

    @Override
    public GetProfilesDto searchByUserAndScientometricSystemId(User user, Integer scientometricSystemId, String fullName, Integer facultyId, Integer chairId, Integer page) {
        List<Profile> profiles = getProfilesByUserAndScientometricSystem(user, scientometricSystemId);

        if (fullName != null && !fullName.isEmpty()) {
            profiles = filterByName(profiles, fullName);
        }

        if (chairId != 0) {
            profiles = filterByChair(profiles, chairId);
        } else if (facultyId != 0) {
            profiles = filterByFaculty(profiles, facultyId);
        }

        return getProfilePageByListAndPage(profiles, page);
    }

    @Override
    public List<LabelDto> getLabelsById(Integer profileId) {
        Profile profile = profileRepository
                .findById(profileId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", profileId));

        return profile.getLabels()
                .stream()
                .map(x -> new LabelDto(x.getId(), x.getName()))
                .toList();
    }

    @Override
    public List<ProfileFieldDto> getProfileFieldValuesById(Integer profileId) {
        Profile profile = profileRepository
                .findById(profileId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", profileId));

        return profile.getProfileFieldValues().stream()
                .map(x -> {
                    FieldTypeDto fieldTypeDto = new FieldTypeDto(x.getField().getType().name());
                    FieldDto fieldDto = new FieldDto(x.getField().getId(),
                            x.getField().getName(), fieldTypeDto);
                    return new ProfileFieldDto(x.getId(), x.getValue(),
                            fieldDto);
                })
                .toList();
    }

    @Override
    public void add(AddProfileDto addProfileDto) {
        Scientist scientist = scientistRepository.findById(addProfileDto.scientistId())
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientist",
                        addProfileDto.scientistId()));

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(addProfileDto.scientometricSystemId())
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        addProfileDto.scientometricSystemId()));

        Optional<Profile> isExistsProfile = profileRepository
                .findByScientometricSystemAndScientist(scientometricSystem, scientist);

        if (isExistsProfile.isPresent()) {
            throw new ProfileScientistScientometricSystemExists();
        }

        List<ProfileFieldValue> profileFieldValues = addProfileDto.profileFields().stream()
                .map(x -> {
                    ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                    profileFieldValue.setField(fieldRepository
                            .findById(x.fieldDto().id())
                            .orElseThrow(() ->
                                    new EntityWithIdNotExistsException("Field", x.fieldDto().id())));
                    profileFieldValue.setValue(x.value());
                    return profileFieldValue;
                })
                .toList();

        Profile profile = new Profile();
        profile.setActive(true);
        profile.setAreWorksDoubtful(false);
        profile.setScientist(scientist);
        profile.setScientometricSystem(scientometricSystem);

        profile.setProfileFieldValues(profileFieldValues);
        profileFieldValues.forEach(x -> x.setProfile(profile));

        labelService.addLabelsToProfile(addProfileDto.labelsId(), profile);

        profileRepository.save(profile);
        profileFieldValueRepository.saveAll(profileFieldValues);
    }

    @Override
    public void edit(Integer id, EditProfileDto editProfileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        List<Field> fields = new ArrayList<>();

        List<ProfileFieldValue> profileFieldValues = editProfileDto.fields().stream()
                .map(x -> {
                    if (x.fieldDto().id() == null) {
                        Field field = new Field();
                        field.setName(x.fieldDto().name());
                        field.setType(getFieldTypeByString(x.fieldDto().fieldType().name()));
                        fields.add(field);

                        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                        profileFieldValue.setField(field);
                        profileFieldValue.setValue(x.value());
                        return profileFieldValue;
                    } else {
                        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                        profileFieldValue.setField(fieldRepository
                                .findById(x.fieldDto().id())
                                .orElseThrow(() ->
                                        new EntityWithIdNotExistsException("Field", x.fieldDto().id())));
                        profileFieldValue.setValue(x.value());
                        return profileFieldValue;
                    }
                })
                .toList();

        profile.setProfileFieldValues(profileFieldValues);
        profileFieldValues.forEach(x -> x.setProfile(profile));

        labelService.addLabelsToProfile(editProfileDto.labelsId(), profile);

        fieldRepository.saveAll(fields);
        profileRepository.save(profile);
        profileFieldValueRepository.saveAll(profileFieldValues);
    }

    @Override
    public void deactivate(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        profile.setActive(false);

        profileRepository.save(profile);
    }

    @Override
    public void activate(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        profile.setActive(true);

        profileRepository.save(profile);
    }

    @Override
    public void markWorksDoubtful(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        profile.setAreWorksDoubtful(true);

        profileRepository.save(profile);
    }

    @Override
    public void unmarkWorksDoubtful(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        profile.setAreWorksDoubtful(false);

        profileRepository.save(profile);
    }

    private List<Profile> filterByName(List<Profile> profiles, String fullname) {
        return profiles.stream()
                .filter(profile -> profile.getScientist().getFullName().toLowerCase()
                        .contains(fullname.toLowerCase().trim()))
                .toList();
    }

    private List<Profile> filterByChair(List<Profile> profiles, Integer chairId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));

        return profiles.stream()
                .filter(profile -> {
                    Chair innerChair = profile.getScientist().getChair();
                    return innerChair != null && innerChair.equals(chair);
                })
                .toList();
    }

    private List<Profile> filterByFaculty(List<Profile> profiles, Integer facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", facultyId));

        return profiles.stream()
                .filter(profile -> {
                    Faculty innerFaculty =
                            profile.getScientist().getFaculty();
                    Chair innerChair = profile.getScientist().getChair();
                    return (innerFaculty != null && innerFaculty.equals(faculty))
                            || (innerChair != null && innerChair.getFaculty().equals(faculty));
                })
                .toList();
    }

    private FieldType getFieldTypeByString(String stringFieldType) {
        return FieldType.valueOf(stringFieldType);
    }

    private ProfilePreviewDto getProfilePreviewDtoByProfile(Profile profile) {
        return new ProfilePreviewDto(profile.getId(), profile.getScientist().getFullName(),
                profile.isAreWorksDoubtful(), profile.isActive());
    }

    private List<Profile> getProfilesByUserAndScientometricSystem(User user, Integer scientometricSystemId) {
        List<Role> roles = user.getRoles();

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        scientometricSystemId));

        Role roleMainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN).orElseThrow();
        Role roleUser = roleRepository.findByName(UserRole.USER).orElseThrow();

        if (roles.contains(roleMainAdmin)) {
            return profileRepository.findByScientometricSystem(scientometricSystem);
        } else if (roles.contains(roleUser)) {
            Scientist scientist = user.getScientists().get(0);
            Profile profile = profileRepository
                    .findByScientometricSystemAndScientist(scientometricSystem, scientist)
                    .orElseThrow();

            return List.of(profile);
        } else {
            Set<Faculty> faculties = user.getFaculties();
            Set<Chair> chairs = user.getChairs();
            List<Profile> profiles = new ArrayList<>();

            for (Faculty faculty : faculties) {
                for (Chair chair : faculty.getChairs()) {
                    chairs.remove(chair);

                    List<Scientist> scientists = chair.getScientists();
                    profiles.addAll(profileRepository
                            .findAllByScientometricSystemAndScientistIn(scientometricSystem,
                                    scientists));
                }
            }

            for (Chair chair : chairs) {
                List<Scientist> scientists = chair.getScientists();
                profiles.addAll(profileRepository
                        .findAllByScientometricSystemAndScientistIn(scientometricSystem,
                                scientists));
            }

            return profiles;
        }
    }

    private GetProfilesDto getProfilePageByListAndPage(List<Profile> profiles, Integer page) {
        int totalPages = (profiles.size() % 25) == 0 ? profiles.size() / 25 : profiles.size() / 25 + 1;

        profiles = profiles.stream()
                .skip((page - 1) * 25L)
                .limit(25)
                .toList();

        List<ProfilePreviewDto> profilePreviewDtos = profiles.stream()
                .map(this::getProfilePreviewDtoByProfile)
                .toList();

        return new GetProfilesDto(profilePreviewDtos, new PageDto(page, totalPages));
    }
}
