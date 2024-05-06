package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.extraction.Extraction;
import org.example.apiapplication.entities.extraction.ExtractionProfile;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.enums.ScientometricSystemName;
import org.example.apiapplication.exceptions.extraction.PreviousExtractionNotFinishedException;
import org.example.apiapplication.exceptions.extraction.TooFrequentExtractionException;
import org.example.apiapplication.helpers.ScholarExtractionHelper;
import org.example.apiapplication.helpers.ScholarQueryBuilder;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.ExtractionService;
import org.example.apiapplication.services.interfaces.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@Transactional
public class ScholarExtractionService implements ExtractionService {
    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;
    private static final String BEAN_NAME = "scholarExtractionService";

    private final ExtractionRepository extractionRepository;
    private final ExtractionProfileRepository extractionProfileRepository;
    private final ProfileRepository profileRepository;
    private final ProfileFieldValueRepository profileFieldValueRepository;
    private final FieldRepository fieldRepository;
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final FieldRuleTypeRepository fieldRuleTypeRepository;
    private final LabelRepository labelRepository;

    private final ScholarExtractionHelper scholarExtractionHelper;

    private final LabelService labelService;

    private final ScientometricSystem scholarScientometricSystem;

    public ScholarExtractionService(ExtractionRepository extractionRepository,
                                    ExtractionProfileRepository extractionProfileRepository,
                                    ProfileRepository profileRepository,
                                    ScientometricSystemRepository scientometricSystemRepository,
                                    ProfileFieldValueRepository profileFieldValueRepository,
                                    FieldRepository fieldRepository, FieldRuleTypeRepository fieldRuleTypeRepository, LabelRepository labelRepository,
                                    ScholarExtractionHelper scholarExtractionHelper, LabelService labelService) {
        this.extractionRepository = extractionRepository;
        this.extractionProfileRepository = extractionProfileRepository;
        this.profileRepository = profileRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
        this.fieldRepository = fieldRepository;
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.fieldRuleTypeRepository = fieldRuleTypeRepository;
        this.labelRepository = labelRepository;

        this.scholarExtractionHelper = scholarExtractionHelper;

        this.labelService = labelService;

        scholarScientometricSystem = scientometricSystemRepository
                .findByName(ScientometricSystemName.SCHOLAR)
                .orElseThrow();
    }

    @Scheduled(initialDelay = 1, fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    @Override
    public void extract() throws IOException {
//        Optional<Extraction> optionalExtraction = extractionRepository
//                .findByScientometricSystemAndIsFinished(scholarScientometricSystem, false);
//
//        if (optionalExtraction.isPresent()) {
//            Extraction extraction = optionalExtraction.get();
//            Optional<ExtractionProfile> optionalExtractionProfile = extractionProfileRepository
//                    .findFirstByExtractionAndIsFinished(extraction, false);
//            if (optionalExtractionProfile.isPresent()) {
//                ExtractionProfile extractionProfile = optionalExtractionProfile.get();
//                Profile profile = extractionProfile.getProfile();
//
//                List<ProfileFieldValue> profileFieldValues = extractScholarProfileFieldValues(profile);
//
//                solveConflicts(profileFieldValues);
//
//                List<Label> labels = labelService.getAllByExtraction(profileFieldValues.stream()
//                        .filter((x) -> x.getField().getRuleType().equals(
//                                fieldRuleTypeRepository.findByName(FieldRuleTypeName.LABELS)
//                                        .orElseThrow(() -> new EntityNotFoundException("Label", x.getValue())))
//                        )
//                        .map(ProfileFieldValue::getValue).toList());
//
//                profile.setLabels(new HashSet<>(labels));
//
//                labelRepository.saveAll(labels);
//
//                extractionProfile.setFinished(true);
//                extractionProfileRepository.save(extractionProfile);
//
//                profileRepository.save(profile);
//                profileFieldValueRepository.saveAll(profileFieldValues);
//            } else {
//                extraction.setFinished(true);
//                extractionRepository.save(extraction);
//                stopExtraction();
//            }
//        } else {
//            stopExtraction();
//        }
    }

    @Override
    public void startExtraction() {
        if (extractionRepository
                .findByScientometricSystemAndIsFinished(scholarScientometricSystem, false)
                .isPresent()) {
            throw new PreviousExtractionNotFinishedException(scholarScientometricSystem.getName().name());
        }

        if (scholarScientometricSystem.getNextMinImportDate().isAfter(LocalDate.now())) {
            throw new TooFrequentExtractionException(scholarScientometricSystem.getName().name(),
                    scholarScientometricSystem.getNextMinImportDate());
        }

        List<Profile> profiles = profileRepository
                .findByScientometricSystem(scholarScientometricSystem);

        if (!profiles.isEmpty()) {
            Extraction extraction = new Extraction();
            extraction.setDateStarted(LocalDate.now());
            extraction.setFinished(false);
            extraction.setScientometricSystem(scholarScientometricSystem);

            List<ExtractionProfile> extractionProfiles = new ArrayList<>();
            for (Profile profile : profiles) {
                ExtractionProfile extractionProfile = new ExtractionProfile();
                extractionProfile.setExtraction(extraction);
                extractionProfile.setFinished(false);
                extractionProfile.setProfile(profile);

                extractionProfiles.add(extractionProfile);
            }

            scholarScientometricSystem.setNextMinImportDate(LocalDate.now().plusMonths(1));
            scientometricSystemRepository.save(scholarScientometricSystem);

            extractionRepository.save(extraction);
            extractionProfileRepository.saveAll(extractionProfiles);

            postProcessor.postProcessAfterInitialization(this, BEAN_NAME);
        }
    }

    @Override
    public void stopExtraction() {
        postProcessor.postProcessBeforeDestruction(this, BEAN_NAME);
    }

    private List<ProfileFieldValue> extractScholarProfileFieldValues(Profile profile) throws IOException {
        List<Field> fields = fieldRepository
                .findByScientometricSystem(scholarScientometricSystem);

        String userId = profile.getProfileUserId();

        ScholarQueryBuilder scholarQueryBuilder = ScholarQueryBuilder.builder()
                .citations()
                .startParameters()
                .hl("uk")
                .view_op("list_works")
                .user(userId)
                .sortBy("pubdate")
                .build();

        List<ProfileFieldValue> profileFieldValues;

        profileFieldValues = scholarExtractionHelper
                .extractScholarProfile(scholarQueryBuilder.toString(), fields);
        profileFieldValues.forEach((x) -> x.setProfile(profile));

        return profileFieldValues;
    }

    private void solveConflicts(List<ProfileFieldValue> profileFieldValues) {
        for (ProfileFieldValue profileFieldValue : profileFieldValues) {
            List<ProfileFieldValue> existingProfileFieldValues = profileFieldValueRepository
                    .findByProfileAndField(profileFieldValue.getProfile(),
                            profileFieldValue.getField());

            // if we have found value for profile for specific field
            if (!existingProfileFieldValues.isEmpty()) {
                if (existingProfileFieldValues.get(0).getKey() != null &&
                        !existingProfileFieldValues.get(0).getKey().isEmpty()) {
                    // if profile field value does have a key
                    Optional<ProfileFieldValue> foundProfileFieldValue =
                            existingProfileFieldValues.stream()
                                    .filter((x) -> x.getKey().equals(profileFieldValue.getKey()))
                                    .findFirst();

                    foundProfileFieldValue.ifPresent(fieldValue ->
                            profileFieldValue.setId(fieldValue.getId()));
                } else {
                    // if profile field value doesn't have a key
                    Optional<ProfileFieldValue> foundProfileFieldValue =
                            existingProfileFieldValues.stream()
                                    .filter((x) -> x.getValue().equals(profileFieldValue.getValue()))
                                    .findFirst();

                    if (foundProfileFieldValue.isEmpty()) {
                        if (existingProfileFieldValues.size() == 1) {
                            profileFieldValue.setId(existingProfileFieldValues.get(0).getId());
                        }
                    } else {
                        profileFieldValue.setId(foundProfileFieldValue.get().getId());
                    }
                }
            }
        }
    }
}
