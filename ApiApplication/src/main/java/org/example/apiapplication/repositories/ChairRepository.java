package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChairRepository extends CrudRepository<Chair, Integer> {
    List<Chair> findByUkrainianNameContainsIgnoreCase(String ukrainianName);

    List<Chair> findByFaculty(Faculty faculty);
}
