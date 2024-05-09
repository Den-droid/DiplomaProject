package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends CrudRepository<Label, Integer>,
        PagingAndSortingRepository<Label, Integer> {
    Optional<Label> findByNameIgnoreCase(String name);

    Page<Label> findByNameContainsIgnoreCase(String name, Pageable pageable);

    Optional<Label> findByNameIgnoreCaseAndIdNot(String name, Integer id);
}
