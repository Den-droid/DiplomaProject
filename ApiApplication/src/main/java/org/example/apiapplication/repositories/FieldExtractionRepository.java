package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.extraction.FieldExtraction;
import org.example.apiapplication.entities.fields.Field;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FieldExtractionRepository extends CrudRepository<FieldExtraction, Integer> {
    Optional<FieldExtraction> findByScientometricSystemAndField(ScientometricSystem scientometricSystem,
                                                                Field field);

    List<FieldExtraction> findByScientometricSystem(ScientometricSystem scientometricSystem);
}
