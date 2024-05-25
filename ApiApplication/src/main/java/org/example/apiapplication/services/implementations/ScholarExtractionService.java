package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.ScientometricSystem;
import org.example.apiapplication.entities.extraction.Extraction;
import org.example.apiapplication.entities.extraction.ExtractionProfile;
import org.example.apiapplication.entities.extraction.FieldExtraction;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.enums.FieldTypeName;
import org.example.apiapplication.enums.ScientometricSystemName;
import org.example.apiapplication.exceptions.extraction.PreviousExtractionNotFinishedException;
import org.example.apiapplication.exceptions.extraction.ProfileByProfileIdNotFoundException;
import org.example.apiapplication.exceptions.extraction.TooFrequentExtractionException;
import org.example.apiapplication.helpers.ScholarExtractionHelper;
import org.example.apiapplication.helpers.ScholarQueryBuilder;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.ExtractionService;
import org.example.apiapplication.services.interfaces.LabelService;
import org.example.apiapplication.services.interfaces.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final LabelRepository labelRepository;
    private final FieldExtractionRepository fieldExtractionRepository;

    private final ScholarExtractionHelper scholarExtractionHelper;

    private final LabelService labelService;
    private final RecommendationService recommendationService;

    private final ScientometricSystem scholarScientometricSystem;

    public ScholarExtractionService(ExtractionRepository extractionRepository,
                                    ExtractionProfileRepository extractionProfileRepository,
                                    ProfileRepository profileRepository,
                                    ScientometricSystemRepository scientometricSystemRepository,
                                    ProfileFieldValueRepository profileFieldValueRepository,
                                    LabelRepository labelRepository,
                                    FieldExtractionRepository fieldExtractionRepository,
                                    ScholarExtractionHelper scholarExtractionHelper,
                                    LabelService labelService,
                                    RecommendationService recommendationService) {
        this.extractionRepository = extractionRepository;
        this.extractionProfileRepository = extractionProfileRepository;
        this.profileRepository = profileRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.labelRepository = labelRepository;
        this.fieldExtractionRepository = fieldExtractionRepository;

        this.scholarExtractionHelper = scholarExtractionHelper;

        this.labelService = labelService;
        this.recommendationService = recommendationService;

        scholarScientometricSystem = scientometricSystemRepository
                .findByName(ScientometricSystemName.SCHOLAR)
                .orElseThrow();
    }

    @Scheduled(initialDelay = 30, fixedDelay = 150, timeUnit = TimeUnit.SECONDS)
    @Override
    public void extract() throws IOException {
        Optional<Extraction> optionalExtraction = extractionRepository
                .findByScientometricSystemAndIsFinished(scholarScientometricSystem, false);

        if (optionalExtraction.isPresent()) {
            Extraction extraction = optionalExtraction.get();
            Optional<ExtractionProfile> optionalExtractionProfile = extractionProfileRepository
                    .findFirstByExtractionAndIsFinishedAndErrorOccurred(extraction, false, false);
            if (optionalExtractionProfile.isPresent()) {
                ExtractionProfile extractionProfile = optionalExtractionProfile.get();
                Profile profile = extractionProfile.getProfile();

                List<ProfileFieldValue> profileFieldValues;
                try {
                    profileFieldValues = extractScholarProfileFieldValues(profile);
                } catch (ProfileByProfileIdNotFoundException e) {
                    extractionProfile.setFinished(false);
                    extractionProfile.setErrorOccurred(true);
                    extractionProfileRepository.save(extractionProfile);
                    return;
                }

                solveConflicts(profileFieldValues);

                List<ProfileFieldValue> labelsProfileFieldValues = profileFieldValues.stream()
                        .filter(x -> x.getField().getType().getName().equals(FieldTypeName.LABEL)).toList();
                profileFieldValues.removeAll(labelsProfileFieldValues);

                List<String> labelsString = labelsProfileFieldValues.stream()
                        .map(ProfileFieldValue::getValue)
                        .toList();
                List<Label> labels = labelService.getAllByExtraction(labelsString);

                profile.setLabels(new HashSet<>(labels));

                labelRepository.saveAll(labels);

                recommendationService.updateRecommendations(profile, profileFieldValues, !labels.isEmpty());

                extractionProfile.setFinished(true);
                extractionProfileRepository.save(extractionProfile);

                profileRepository.save(profile);
                profileFieldValueRepository.saveAll(profileFieldValues);
            } else {
                extraction.setFinished(true);
                extractionRepository.save(extraction);
                stopExtraction();
            }
        } else {
            stopExtraction();
        }
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
                extractionProfile.setErrorOccurred(false);
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

    private List<ProfileFieldValue> extractScholarProfileFieldValues(Profile profile) throws IOException, ProfileByProfileIdNotFoundException {
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

        List<FieldExtraction> fieldExtractions = fieldExtractionRepository.
                findByScientometricSystem(scholarScientometricSystem);

        try {
            profileFieldValues = scholarExtractionHelper
                    .extractScholarProfile(scholarQueryBuilder.toString(), fieldExtractions);
        } catch (Exception e) {
            throw new ProfileByProfileIdNotFoundException(userId);
        }
        profileFieldValues.forEach((x) -> x.setProfile(profile));

        return profileFieldValues;
    }

    private void solveConflicts(List<ProfileFieldValue> profileFieldValues) {
        for (ProfileFieldValue profileFieldValue : profileFieldValues) {
            Optional<ProfileFieldValue> optionalProfileFieldValue = profileFieldValueRepository
                    .findByProfileAndField(profileFieldValue.getProfile(),
                            profileFieldValue.getField());

            // if we have found value for profile for specific field
            if (optionalProfileFieldValue.isPresent()) {
                ProfileFieldValue existingProfileFieldValue = optionalProfileFieldValue.get();
                profileFieldValue.setId(existingProfileFieldValue.getId());
            }
        }
    }
}
