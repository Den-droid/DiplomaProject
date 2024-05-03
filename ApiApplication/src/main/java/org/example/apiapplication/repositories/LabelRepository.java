package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends CrudRepository<Label, Integer>,
        PagingAndSortingRepository<Label, Integer> {
    Page<Label> findByName(String name, Pageable pageable);

    Optional<Label> findByNameIgnoreCase(String name);

    Page<Label> findByNameContainsIgnoreCase(String name, Pageable pageable);

    List<Label> findByNameContainsIgnoreCase(String name);

    Optional<Label> findByNameIgnoreCaseAndIdNot(String name, Integer id);

}
