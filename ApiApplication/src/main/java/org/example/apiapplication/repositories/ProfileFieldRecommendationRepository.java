package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileFieldRecommendationRepository extends CrudRepository<ProfileFieldRecommendation, Integer> {
}
