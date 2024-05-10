package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.scientist.EditScientistDto;
import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.repositories.ScientistRepository;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ScientistServiceImpl implements ScientistService {
    private final FacultyRepository facultyRepository;
    private final ChairRepository chairRepository;
    private final ScientistRepository scientistRepository;
    private final RoleRepository roleRepository;

    public ScientistServiceImpl(FacultyRepository facultyRepository,
                                ChairRepository chairRepository,
                                ScientistRepository scientistRepository,
                                RoleRepository roleRepository) {
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.scientistRepository = scientistRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void edit(Integer id, EditScientistDto editScientistDto) {
        Scientist scientist = scientistRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientist", id));

        scientist.setPosition(editScientistDto.position());
        scientist.setFullName(editScientistDto.fullName());
        if (scientist.getUser() != null)
            scientist.getUser().setFullName(editScientistDto.fullName());

        if (editScientistDto.chairId() != null) {
            Chair chair = chairRepository.findById(id)
                    .orElseThrow(() -> new EntityWithIdNotExistsException("Chair", id));

            scientist.setChair(chair);
        } else if (editScientistDto.facultyId() != null) {
            Faculty faculty = facultyRepository.findById(id)
                    .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", id));

            scientist.setFaculty(faculty);
        }

        scientistRepository.save(scientist);
    }

    @Override
    public List<ScientistPreviewDto> getAllScientistPreview() {
        List<Scientist> scientists = scientistRepository.findAllByUserNull();
        return scientists.stream()
                .map((x) -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                .toList();
    }

    @Override
    public List<ScientistPreviewDto> getAllScientistPreviewByUser(User user) {
        String userRole = user.getRoles().get(0).getName().name();

        if (userRole.equals(UserRole.MAIN_ADMIN.name())) {
            return getAllScientistPreview();
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
}
