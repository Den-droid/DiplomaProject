package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.extraction.Extraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtractionRepository extends CrudRepository<Extraction, Integer> {
    Optional<Extraction> findByScientometricSystemAndIsFinished(ScientometricSystem scientometricSystem,
                                                                boolean finished);

    boolean existsByScientometricSystemAndIsFinished(ScientometricSystem scientometricSystem,
                                                   boolean finished);

    List<Extraction> findAllByScientometricSystem(ScientometricSystem scientometricSystem);
}
