package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileFieldValueRepository extends CrudRepository<ProfileFieldValue, Integer> {
    Optional<ProfileFieldValue> findByProfileAndField(Profile profile, Field field);
}
