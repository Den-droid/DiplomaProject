package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileFieldRecommendationRepository extends CrudRepository<ProfileFieldRecommendation, Integer> {
    Optional<ProfileFieldRecommendation> findByProfileAndField(Profile profile, Field field);
}
