package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Scientist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScientistRepository extends CrudRepository<Scientist, Integer> {
    List<Scientist> findAllByUserNull();
}
