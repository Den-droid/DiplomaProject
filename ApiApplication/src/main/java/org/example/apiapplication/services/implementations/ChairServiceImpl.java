package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.services.interfaces.ChairService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ChairServiceImpl implements ChairService {
    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;

    public ChairServiceImpl(ChairRepository chairRepository,
                            FacultyRepository facultyRepository) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
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
    public List<ChairDto> getByFaculty(Integer id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, id));

        return getByFaculty(faculty);
    }

    @Override
    public List<ChairDto> getByFaculty(Faculty faculty) {
        List<Chair> chairs = faculty.getChairs();

        return chairs.stream()
                .map(x -> new ChairDto(x.getId(), x.getUkrainianName(), x.getFaculty().getId()))
                .toList();
    }

    @Override
    public ChairDto getById(Integer id) {
        Chair chair = chairRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.CHAIR, id));
        return new ChairDto(chair.getId(), chair.getUkrainianName(), chair.getFaculty().getId());
    }
}
