package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Faculty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyRepository extends CrudRepository<Faculty, Integer> {
    List<Faculty> findByUkrainianNameContainsIgnoreCase(String ukrainianName);
}
