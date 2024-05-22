package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;
import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.indices.IndicesDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.dto.profile.*;
import org.example.apiapplication.entities.*;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldType;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.FieldTypeName;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityNotFoundException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.exceptions.profile.ProfileScientistScientometricSystemExists;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.LabelService;
import org.example.apiapplication.services.interfaces.ProfileService;
import org.example.apiapplication.services.interfaces.RecommendationService;
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
    private final ProfileFieldRecommendationRepository profileFieldRecommendationRepository;
    private final LabelRepository labelRepository;

    private final LabelService labelService;
    private final RecommendationService recommendationService;

    public ProfileServiceImpl(ScientometricSystemRepository scientometricSystemRepository,
                              ProfileRepository profileRepository,
                              ScientistRepository scientistRepository,
                              FieldRepository fieldRepository,
                              ProfileFieldValueRepository profileFieldValueRepository,
                              RoleRepository roleRepository,
                              ChairRepository chairRepository,
                              FacultyRepository facultyRepository,
                              ProfileFieldRecommendationRepository profileFieldRecommendationRepository,
                              LabelRepository labelRepository,
                              LabelService labelService,
                              RecommendationService recommendationService) {
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.profileRepository = profileRepository;
        this.scientistRepository = scientistRepository;
        this.fieldRepository = fieldRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
        this.roleRepository = roleRepository;
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
        this.profileFieldRecommendationRepository = profileFieldRecommendationRepository;
        this.labelRepository = labelRepository;

        this.labelService = labelService;
        this.recommendationService = recommendationService;
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
                    Field field = x.getField();
                    FieldType fieldType = x.getField().getType();
                    FieldTypeDto fieldTypeDto = new FieldTypeDto(fieldType.getId(),
                            fieldType.getName().name());
                    FieldDto fieldDto = new FieldDto(field.getId(),
                            field.getName(), fieldTypeDto);
                    return new ProfileFieldDto(x.getId(), x.getValue(),
                            fieldDto);
                })
                .toList();
    }

    @Override
    public ProfileFullDto getProfileFullById(Integer id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        return getProfileFullDtoByProfile(profile);
    }

    @Override
    public boolean canProfileBeAddedToSystemAndScientist(Integer scientistId, Integer scientometricSystemId) {
        Scientist scientist = scientistRepository.findById(scientistId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientist",
                        scientistId));

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        scientometricSystemId));

        Optional<Profile> isExistsProfile = profileRepository
                .findByScientometricSystemAndScientist(scientometricSystem, scientist);

        return isExistsProfile.isEmpty();
    }

    @Override
    public List<ProfileByLabelDto> getProfilesByLabelId(Integer labelId) {
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Label", labelId));

        List<Profile> profiles = label.getProfiles();

        return profiles.stream()
                .map(x -> {
                    String faculty, chair;
                    if (x.getScientist().getFaculty() != null) {
                        faculty = x.getScientist().getFaculty().getUkrainianAbbreviation();
                        chair = "";
                    } else {
                        Scientist scientist = x.getScientist();
                        faculty = scientist.getChair().getFaculty().getUkrainianAbbreviation();
                        chair = scientist.getChair().getUkrainianAbbreviation();
                    }
                    String name = x.getScientist().getFullName();
                    String scientometricSystem = x.getScientometricSystem().getName().name();

                    return new ProfileByLabelDto(name, scientometricSystem, faculty, chair);
                })
                .toList();
    }

    @Override
    public List<ProfileForUserDto> getProfilesForUser(Integer scientometricSystemId, Integer chairId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", chairId));

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        scientometricSystemId));

        List<Scientist> scientists = new ArrayList<>(chair.getScientists());
        List<Profile> profiles = profileRepository
                .findAllByScientometricSystemAndScientistInAndAreWorksDoubtful(
                        scientometricSystem, scientists, false);

        List<ProfileForUserDto> profileForUserDtos = new ArrayList<>();
        for (Profile profile : profiles) {
            List<ProfileFieldValue> profileFieldValues = profile.getProfileFieldValues();
            int citation = -1, hirsh = -1;
            boolean citationDone = false, hirshDone = false;

            for (ProfileFieldValue profileFieldValue : profileFieldValues) {
                if (!citationDone && profileFieldValue.getField().getType().getName()
                        .equals(FieldTypeName.CITATION)) {
                    if (!profileFieldValue.getValue().isEmpty())
                        citation = Integer.parseInt(profileFieldValue.getValue());

                    citationDone = true;
                } else if (!hirshDone && profileFieldValue.getField().getType().getName()
                        .equals(FieldTypeName.H_INDEX)) {
                    if (!profileFieldValue.getValue().isEmpty())
                        hirsh = Integer.parseInt(profileFieldValue.getValue());

                    hirshDone = true;
                }

                if (citationDone && hirshDone)
                    break;
            }
            List<String> recommendations = recommendationService.getByProfile(profile);

            profileForUserDtos.add(new ProfileForUserDto(profile.getScientist().getFullName(),
                    new IndicesDto(citation, hirsh), recommendations));
        }

        return profileForUserDtos;
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
                    Field field = fieldRepository.findById(x.field().id())
                            .orElseThrow(() ->
                                    new EntityWithIdNotExistsException("Field", x.field().id()));

                    ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                    profileFieldValue.setField(field);
                    profileFieldValue.setValue(x.value());
                    return profileFieldValue;
                })
                .toList();

        Profile profile = new Profile();
        profile.setActive(true);
        profile.setAreWorksDoubtful(false);
        profile.setScientist(scientist);
        profile.setScientometricSystem(scientometricSystem);

        profileFieldValues.forEach(x -> x.setProfile(profile));

        labelService.addLabelsToProfile(addProfileDto.labelsIds(), profile);
        recommendationService.updateRecommendations(profile, profileFieldValues, !addProfileDto.labelsIds().isEmpty());

        profileRepository.save(profile);
        profileFieldValueRepository.saveAll(profileFieldValues);
    }

    @Override
    public void edit(Integer id, EditProfileDto editProfileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Profile", id));

        List<ProfileFieldValue> profileFieldValues = editProfileDto.fields().stream()
                .map(x -> {
                    if (x.id() == -1) {
                        Field field = fieldRepository.findById(x.field().id())
                                .orElseThrow(() ->
                                        new EntityWithIdNotExistsException("Field", x.field().id()));

                        ProfileFieldValue profileFieldValue = new ProfileFieldValue();
                        profileFieldValue.setField(field);
                        profileFieldValue.setValue(x.value());
                        profileFieldValue.setProfile(profile);
                        return profileFieldValue;
                    } else {
                        ProfileFieldValue profileFieldValue = profileFieldValueRepository
                                .findById(x.id()).orElseThrow(() -> new
                                        EntityWithIdNotExistsException("ProfileFieldValue", x.id()));
                        profileFieldValue.setValue(x.value());
                        return profileFieldValue;
                    }
                })
                .toList();

        List<ProfileFieldValue> originalProfileFieldValues = profile.getProfileFieldValues();
        List<ProfileFieldValue> profileFieldValuesToRemove = new ArrayList<>();
        List<ProfileFieldRecommendation> profileFieldRecommendationsToRemove = new ArrayList<>();

        for (ProfileFieldValue profileFieldValue : originalProfileFieldValues) {
            if (!profileFieldValues.contains(profileFieldValue)
                    && profileFieldValue.getField().getType().getName() != FieldTypeName.LABEL) {
                profileFieldValuesToRemove.add(profileFieldValue);

                ProfileFieldRecommendation profileFieldRecommendation =
                        profileFieldRecommendationRepository.findByProfileAndField(
                                        profileFieldValue.getProfile(), profileFieldValue.getField())
                                .orElseThrow(() -> new EntityNotFoundException("Profile Recommendation",
                                        profileFieldValue.getField().getName()));

                profileFieldRecommendationsToRemove.add(profileFieldRecommendation);
            }
        }

        // update labels and recommendations
        labelService.addLabelsToProfile(editProfileDto.labelsIds(), profile);
        recommendationService.updateRecommendations(profile, profileFieldValues,
                !editProfileDto.labelsIds().isEmpty());

        profileFieldRecommendationRepository.deleteAll(profileFieldRecommendationsToRemove);
        profileFieldValueRepository.deleteAll(profileFieldValuesToRemove);
        profileFieldValueRepository.saveAll(profileFieldValues);
        profileRepository.save(profile);
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

    private ProfilePreviewDto getProfilePreviewDtoByProfile(Profile profile) {
        return new ProfilePreviewDto(profile.getId(), profile.getScientist().getFullName(),
                profile.isAreWorksDoubtful(), profile.isActive());
    }

    private ProfileFullDto getProfileFullDtoByProfile(Profile profile) {
        List<ProfileFieldDto> profileFieldDtos = profile.getProfileFieldValues()
                .stream()
                .map(x -> {
                    Field field = x.getField();
                    FieldType fieldType = x.getField().getType();
                    FieldDto fieldDto = new FieldDto(field.getId(), field.getName(),
                            new FieldTypeDto(fieldType.getId(), fieldType.getName().name()));
                    return new ProfileFieldDto(x.getId(), x.getValue(), fieldDto);
                })
                .toList();

        List<LabelDto> labelDtos = profile.getLabels()
                .stream()
                .map(x -> new LabelDto(x.getId(), x.getName()))
                .toList();

        return new ProfileFullDto(profile.getId(),
                profile.isAreWorksDoubtful(), profile.isActive(), profileFieldDtos, labelDtos);
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
