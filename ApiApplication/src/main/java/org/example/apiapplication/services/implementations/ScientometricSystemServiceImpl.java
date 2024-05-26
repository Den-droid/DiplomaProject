package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.scientometric_system.ExtractionErrorsDto;
import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.extraction.Extraction;
import org.example.apiapplication.entities.extraction.ExtractionProfile;
import org.example.apiapplication.enums.ExtractionStatus;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.ExtractionProfileRepository;
import org.example.apiapplication.repositories.ExtractionRepository;
import org.example.apiapplication.repositories.ScientometricSystemRepository;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScientometricSystemServiceImpl implements ScientometricSystemService {
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final ExtractionRepository extractionRepository;
    private final ExtractionProfileRepository extractionProfileRepository;

    public ScientometricSystemServiceImpl(ScientometricSystemRepository scientometricSystemRepository,
                                          ExtractionRepository extractionRepository,
                                          ExtractionProfileRepository extractionProfileRepository) {
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.extractionRepository = extractionRepository;
        this.extractionProfileRepository = extractionProfileRepository;
    }

    @Override
    public List<ScientometricSystemDto> getAll() {
        List<ScientometricSystemDto> scientometricSystemDtos = new ArrayList<>();
        for (ScientometricSystem scientometricSystem : scientometricSystemRepository.findAll()) {
            scientometricSystemDtos.add(new ScientometricSystemDto(scientometricSystem.getId(),
                    scientometricSystem.getName().name(), scientometricSystem.getNextMinImportDate()));
        }
        return scientometricSystemDtos;
    }

    @Override
    public boolean isExtractionRunningById(Integer id) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTOMETRIC_SYSTEM, id));

        return extractionRepository.existsByScientometricSystemAndIsFinished(scientometricSystem,
                false);
    }

    @Override
    public boolean isExtractionPossibleById(Integer id) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTOMETRIC_SYSTEM, id));

        return LocalDate.now().isAfter(scientometricSystem.getNextMinImportDate());
    }

    @Override
    public ExtractionErrorsDto getExtractionErrorsById(Integer id) {
        ScientometricSystem scientometricSystem = scientometricSystemRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SCIENTOMETRIC_SYSTEM, id));

        List<Extraction> extractions = extractionRepository
                .findAllByScientometricSystem(scientometricSystem);

        if (!extractions.isEmpty()) {
            Optional<Extraction> isNotFinishedExtraction = extractions.stream()
                    .filter(x -> !x.isFinished())
                    .findFirst();

            Extraction extraction;
            extraction = isNotFinishedExtraction.orElseGet(() -> extractions.stream()
                    .min(Comparator.comparing(Extraction::getDateStarted))
                    .orElse(new Extraction()));

            List<ExtractionProfile> extractionProfiles = extractionProfileRepository
                    .findAllByExtractionAndErrorOccurred(extraction, true);
            List<String> scientistNames = extractionProfiles.stream()
                    .map(x -> x.getProfile().getScientist().getFullName())
                    .toList();

            String status;
            if (!scientistNames.isEmpty()) {
                status = ExtractionStatus.ERRORS_OCCURRED.name();
            } else {
                status = ExtractionStatus.NO_ERRORS.name();
            }

            return new ExtractionErrorsDto(status, scientistNames);
        } else {
            return new ExtractionErrorsDto(ExtractionStatus.NO_EXTRACTIONS.name(), new ArrayList<>());
        }
    }
}
