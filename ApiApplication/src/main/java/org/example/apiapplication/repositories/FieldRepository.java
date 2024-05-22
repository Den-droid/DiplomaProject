package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRepository extends CrudRepository<Field, Integer>,
        PagingAndSortingRepository<Field, Integer> {
    Page<Field> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

    Optional<Field> findByNameIgnoreCaseAndIdNot(String name, Integer id);

    Optional<Field> findByNameIgnoreCase(String name);

    Optional<Field> findByType(FieldType type);
}
