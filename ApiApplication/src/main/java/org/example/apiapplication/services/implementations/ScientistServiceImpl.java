package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.scientist.EditScientistDto;
import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.Scientist;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.repositories.ScientistRepository;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ScientistServiceImpl implements ScientistService {
    private final FacultyRepository facultyRepository;
    private final ChairRepository chairRepository;
    private final ScientistRepository scientistRepository;

    public ScientistServiceImpl(FacultyRepository facultyRepository,
                                ChairRepository chairRepository,
                                ScientistRepository scientistRepository) {
        this.facultyRepository = facultyRepository;
        this.chairRepository = chairRepository;
        this.scientistRepository = scientistRepository;
    }

    @Override
    public void edit(Integer id, EditScientistDto editScientistDto) {
        Scientist scientist = scientistRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTIST, id));

        scientist.setPosition(editScientistDto.position());
        scientist.setFullName(editScientistDto.fullName());
        if (scientist.getUser() != null)
            scientist.getUser().setFullName(editScientistDto.fullName());

        if (editScientistDto.chairId() != null) {
            Chair chair = chairRepository.findById(id)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.CHAIR, id));

            scientist.setChair(chair);
        } else if (editScientistDto.facultyId() != null) {
            Faculty faculty = facultyRepository.findById(id)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FACULTY, id));

            scientist.setFaculty(faculty);
        }

        scientistRepository.save(scientist);
    }

    @Override
    public List<ScientistPreviewDto> getNotRegisteredScientists() {
        List<Scientist> scientists = scientistRepository.findAllByUserNull();
        return scientists.stream()
                .map((x) -> new ScientistPreviewDto(x.getId(), x.getFullName()))
                .toList();
    }
}
