package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.enums.ScientometricSystemName;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScientometricSystemRepository extends CrudRepository<ScientometricSystem, Integer> {
    Optional<ScientometricSystem> findByName(ScientometricSystemName name);
}
