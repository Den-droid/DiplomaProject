package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.services.interfaces.FacultyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
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
}
