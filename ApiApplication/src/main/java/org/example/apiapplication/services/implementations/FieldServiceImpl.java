package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.enums.FieldType;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.repositories.FieldRepository;
import org.example.apiapplication.repositories.ScientometricSystemRepository;
import org.example.apiapplication.services.interfaces.FieldService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class FieldServiceImpl implements FieldService {
    private final FieldRepository fieldRepository;
    private final ScientometricSystemRepository scientometricSystemRepository;

    public FieldServiceImpl(FieldRepository fieldRepository,
                            ScientometricSystemRepository scientometricSystemRepository) {
        this.fieldRepository = fieldRepository;
        this.scientometricSystemRepository = scientometricSystemRepository;
    }

    @Override
    public List<FieldTypeDto> getFieldTypes() {
        return Arrays.stream(FieldType.values())
                .map(x -> new FieldTypeDto(x.name()))
                .toList();
    }

    @Override
    public List<FieldDto> getFieldsByScientometricSystemId(Integer scientometricSystemId) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository
                .findById(scientometricSystemId)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Scientometric System",
                        scientometricSystemId));

        return fieldRepository.findByScientometricSystem(scientometricSystem)
                .stream()
                .map(x -> new FieldDto(x.getId(), x.getName(),
                        new FieldTypeDto(x.getType().name())))
                .toList();
    }
}
