package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Label;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends CrudRepository<Label, Integer> {
    Optional<Label> findByName(String name);

    Optional<Label> findByNameAndIdNot(String name, Integer id);

}
