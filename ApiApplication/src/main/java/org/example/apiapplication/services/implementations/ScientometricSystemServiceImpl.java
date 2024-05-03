package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.ExtractionRepository;
import org.example.apiapplication.repositories.ScientometricSystemRepository;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScientometricSystemServiceImpl implements ScientometricSystemService {
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final ExtractionRepository extractionRepository;

    public ScientometricSystemServiceImpl(ScientometricSystemRepository scientometricSystemRepository,
                                          ExtractionRepository extractionRepository) {
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.extractionRepository = extractionRepository;
    }

    @Override
    public List<ScientometricSystemDto> getAll() {
        List<ScientometricSystemDto> scientometricSystemDtos = new ArrayList<>();
        for (ScientometricSystem scientometricSystem : scientometricSystemRepository.findAll()) {
            scientometricSystemDtos.add(new ScientometricSystemDto(scientometricSystem.getId(),
                    scientometricSystem.getName().name(), scientometricSystem.getNextMinImportDate()));
        }
        return scientometricSystemDtos;
    }

    @Override
    public boolean isExtractionRunningById(Integer id) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System", id));

        return extractionRepository.existsByScientometricSystemAndIsFinished(scientometricSystem,
                false);
    }

    @Override
    public boolean isExtractionPossibleById(Integer id) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System", id));

        return LocalDate.now().isAfter(scientometricSystem.getNextMinImportDate());
    }
}
