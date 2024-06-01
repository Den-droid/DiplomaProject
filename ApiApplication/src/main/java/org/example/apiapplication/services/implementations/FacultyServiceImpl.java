package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.dto.indices.EntityIndicesDto;
import org.example.apiapplication.dto.indices.IndicesDto;
import org.example.apiapplication.entities.*;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.FieldTypeName;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.repositories.ProfileRepository;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.repositories.ScientometricSystemRepository;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.FacultyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;

    private final SessionUtil sessionUtil;

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              ScientometricSystemRepository scientometricSystemRepository,
                              ProfileRepository profileRepository,
                              RoleRepository roleRepository,
                              SessionUtil sessionUtil) {
        this.facultyRepository = facultyRepository;
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public List<FacultyDto> getAll() {
        List<Faculty> faculties = new ArrayList<>();
        for (Faculty faculty : facultyRepository.findAll()) {
            faculties.add(faculty);
        }

        return faculties.stream()
                .map(x -> new FacultyDto(x.getId(), x.getUkrainianName()))
                .toList();
    }

    @Override
    public List<EntityIndicesDto> getFacultiesIndices(Integer scientometricSystemId) {
        List<Faculty> faculties = new ArrayList<>();
        for (Faculty faculty : facultyRepository.findAll()) {
            faculties.add(faculty);
        }

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTOMETRIC_SYSTEM,
                        scientometricSystemId));

        List<Scientist> scientists = new ArrayList<>();
        List<EntityIndicesDto> facultiesIndices = new ArrayList<>();

        for (Faculty faculty : faculties) {
            scientists.addAll(faculty.getScientists());
            for (Chair chair : faculty.getChairs()) {
                scientists.addAll(chair.getScientists());
            }

            List<Profile> profiles = profileRepository
                    .findAllByScientometricSystemAndScientistInAndAreWorksDoubtfulAndIsActive(
                            scientometricSystem, scientists, false, true);

            int[] indices = getIndicesSumByProfiles(profiles);

            facultiesIndices.add(new EntityIndicesDto(faculty.getUkrainianAbbreviation(),
                    new IndicesDto(indices[0], indices[1])));

            scientists.clear();
        }

        return facultiesIndices;
    }

    @Override
    public List<EntityIndicesDto> getChairsIndicesByFaculty(Integer facultyId, Integer scientometricSystemId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, facultyId));

        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTOMETRIC_SYSTEM,
                        scientometricSystemId));

        List<EntityIndicesDto> chairsIndices = new ArrayList<>();
        List<Scientist> scientists = new ArrayList<>();

        for (Chair chair : faculty.getChairs()) {
            scientists.addAll(chair.getScientists());

            List<Profile> profiles = profileRepository
                    .findAllByScientometricSystemAndScientistInAndAreWorksDoubtfulAndIsActive(
                            scientometricSystem, scientists, false, true);

            int[] indices = getIndicesSumByProfiles(profiles);

            chairsIndices.add(new EntityIndicesDto(chair.getUkrainianAbbreviation(),
                    new IndicesDto(indices[0], indices[1])));

            scientists.clear();
        }

        return chairsIndices;
    }

    @Override
    public List<FacultyDto> getForCurrentUser() {
        User user = sessionUtil.getUserFromSession();

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
            return getAll();
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

    private int[] getIndicesSumByProfiles(List<Profile> profiles) {
        int[] indices = new int[2];
        indices = profiles.stream()
                .map(x -> {
                    int citation = 0, hirsh = 0;
                    boolean citationDone = false, hirshDone = false;
                    for (ProfileFieldValue profileFieldValue : x.getProfileFieldValues()) {
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
                    return new int[]{citation, hirsh};
                })
                .reduce(indices,
                        (previous, next) -> new int[]{previous[0] + next[0], previous[1] + next[1]},
                        (previous, next) -> new int[]{previous[0] + next[0], previous[1] + next[1]});

        return indices;
    }
}
