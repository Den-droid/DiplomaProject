package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.recommendation.FieldRecommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRecommendationRepository extends CrudRepository<FieldRecommendation, Integer> {
    Optional<FieldRecommendation> findByFieldAndScientometricSystem(Field field,
                                                                    ScientometricSystem scientometricSystem);
}
