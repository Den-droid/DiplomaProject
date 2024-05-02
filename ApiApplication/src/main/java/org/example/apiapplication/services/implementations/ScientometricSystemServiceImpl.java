package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.repositories.ScientometricSystemRepository;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScientometricSystemServiceImpl implements ScientometricSystemService {
    private final ScientometricSystemRepository scientometricSystemRepository;

    public ScientometricSystemServiceImpl(ScientometricSystemRepository scientometricSystemRepository) {
        this.scientometricSystemRepository = scientometricSystemRepository;
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
}
