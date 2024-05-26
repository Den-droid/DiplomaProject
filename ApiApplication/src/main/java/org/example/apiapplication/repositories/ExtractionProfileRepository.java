package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.extraction.Extraction;
import org.example.apiapplication.entities.extraction.ExtractionProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtractionProfileRepository extends CrudRepository<ExtractionProfile, Integer> {
    Optional<ExtractionProfile> findFirstByExtractionAndIsFinishedAndErrorOccurred(Extraction extraction,
                                                                                   boolean finished, boolean errorOccurred);

    List<ExtractionProfile> findAllByExtractionAndErrorOccurred(Extraction extraction, boolean occurred);
}
