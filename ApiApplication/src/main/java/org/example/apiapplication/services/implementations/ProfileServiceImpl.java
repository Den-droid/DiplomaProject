package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;
import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.AddProfileDto;
import org.example.apiapplication.dto.profile.EditProfileDto;
import org.example.apiapplication.dto.profile.ProfilePreviewDto;
import org.example.apiapplication.entities.*;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.FieldType;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.LabelService;
import org.example.apiapplication.services.interfaces.ProfileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    private final LabelService labelService;

    public ProfileServiceImpl(ScientometricSystemRepository scientometricSystemRepository,
                              ProfileRepository profileRepository,
                              ScientistRepository scientistRepository,
                              FieldRepository fieldRepository,
                              ProfileFieldValueRepository profileFieldValueRepository,
                              LabelService labelService, RoleRepository roleRepository) {
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.profileRepository = profileRepository;
        this.scientistRepository = scientistRepository;
        this.fieldRepository = fieldRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
        this.roleRepository = roleRepository;

        this.labelService = labelService;
    }

    @Override
    public List<ProfilePreviewDto> getByUserAndScientometricSystemId(User user, Integer scientometricSystemId) {
        List<Role> roles = user.getRoles();

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        scientometricSystemId));

        Role roleMainAdmin = roleRepository.findByName(UserRole.MAIN_ADMIN).orElseThrow();
        Role roleUser = roleRepository.findByName(UserRole.USER).orElseThrow();

        if (roles.contains(roleMainAdmin)) {
            return profileRepository.findByScientometricSystem(scientometricSystem)
                    .stream()
                    .map(this::getProfilePreviewDtoByProfile)
                    .toList();
        } else if (roles.contains(roleUser)) {
            Scientist scientist = user.getUser().get(0);
            Profile profile = profileRepository
                    .findByScientometricSystemAndScientist(scientometricSystem, scientist)
                    .orElseThrow();

            return List.of(getProfilePreviewDtoByProfile(profile));
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

            return profiles.stream()
                    .map(this::getProfilePreviewDtoByProfile)
                    .toList();
        }
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
        profile.setDeactivated(false);
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

        profile.setDeactivated(false);

        profileRepository.save(profile);
    }

    @Override
    public void activate(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        profile.setDeactivated(true);

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

    private FieldType getFieldTypeByString(String stringFieldType) {
        return FieldType.valueOf(stringFieldType);
    }

    private ProfilePreviewDto getProfilePreviewDtoByProfile(Profile profile) {
        return new ProfilePreviewDto(profile.getId(), profile.getScientist().getFullName(),
                profile.isAreWorksDoubtful(), profile.isDeactivated());
    }
}
