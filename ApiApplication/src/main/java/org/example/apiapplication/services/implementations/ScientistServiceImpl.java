package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.repositories.ScientistRepository;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ScientistServiceImpl implements ScientistService {
    private final ScientistRepository scientistRepository;
    private final SessionUtil sessionUtil;

    public ScientistServiceImpl(ScientistRepository scientistRepository,
                                SessionUtil sessionUtil) {
        this.scientistRepository = scientistRepository;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public List<ScientistPreviewDto> getNotRegisteredScientists() {
        List<Scientist> scientists = scientistRepository.findAllByUserNull();
        return scientists.stream()
                .map((x) -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                .toList();
    }

    @Override
    public List<ScientistPreviewDto> getForCurrentUser() {
        User user = sessionUtil.getUserFromSession();
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
}
