package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.ScientometricSystem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends CrudRepository<Field, Integer> {
    List<Field> findByScientometricSystem(ScientometricSystem scientometricSystem);
}
