package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.fields.FieldRuleType;
import org.example.apiapplication.enums.FieldRuleTypeName;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRuleTypeRepository extends CrudRepository<FieldRuleType, Integer> {
    Optional<FieldRuleType> findByName(FieldRuleTypeName name);
}
