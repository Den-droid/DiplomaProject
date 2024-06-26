package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.ChairService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChairServiceImpl implements ChairService {
    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;
    private final RoleRepository roleRepository;

    private final SessionUtil sessionUtil;

    public ChairServiceImpl(ChairRepository chairRepository,
                            FacultyRepository facultyRepository,
                            RoleRepository roleRepository,
                            SessionUtil sessionUtil) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
        this.roleRepository = roleRepository;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public List<ChairDto> getAll() {
        List<Chair> chairs = new ArrayList<>();
        for (Chair chair : chairRepository.findAll()) {
            chairs.add(chair);
        }

        return chairs.stream()
                .map(x -> new ChairDto(x.getId(), x.getUkrainianName(), x.getFaculty().getId()))
                .toList();
    }

    @Override
    public List<ChairDto> getByFaculty(Integer facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, facultyId));

        List<Chair> chairs = faculty.getChairs();

        return chairs.stream()
                .map(x -> new ChairDto(x.getId(), x.getUkrainianName(), x.getFaculty().getId()))
                .toList();
    }

    @Override
    public List<ChairDto> getForCurrentUser() {
        User user = sessionUtil.getUserFromSession();

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
            return getAll();
        } else {
            Chair chair = user.getScientists().get(0).getChair();
            return List.of(new ChairDto(chair.getId(), chair.getUkrainianName(),
                    chair.getFaculty().getId()));
        }
    }
}
