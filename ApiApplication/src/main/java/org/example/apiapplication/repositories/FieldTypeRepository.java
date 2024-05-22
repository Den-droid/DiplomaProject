package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.fields.FieldType;
import org.example.apiapplication.enums.FieldTypeName;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FieldTypeRepository extends CrudRepository<FieldType, Integer> {
    Optional<FieldType> findByName(FieldTypeName name);
}
