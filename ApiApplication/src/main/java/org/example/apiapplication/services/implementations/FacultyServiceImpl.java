package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.repositories.RoleRepository;
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
    private final ChairRepository chairRepository;
    private final RoleRepository roleRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              ChairRepository chairRepository,
                              RoleRepository roleRepository) {
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.roleRepository = roleRepository;
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
    public FacultyDto getById(Integer id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Faculty", id));

        return new FacultyDto(faculty.getId(), faculty.getUkrainianName());
    }

    @Override
    public List<FacultyDto> getByUser(User user) {
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
            Faculty faculty = user.getScientists().get(0).getFaculty();
            return List.of(new FacultyDto(faculty.getId(), faculty.getUkrainianName()));
        }
    }
}
