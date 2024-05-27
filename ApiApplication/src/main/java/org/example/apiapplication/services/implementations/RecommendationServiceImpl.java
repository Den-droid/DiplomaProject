package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldType;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.recommendation.FieldRecommendation;
import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;
import org.example.apiapplication.enums.FieldTypeName;
import org.example.apiapplication.exceptions.entity.EntityNotFoundException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.RecommendationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {
    private final FieldRecommendationRepository fieldRecommendationRepository;
    private final ProfileFieldRecommendationRepository profileFieldRecommendationRepository;
    private final FieldTypeRepository fieldTypeRepository;
    private final FieldRepository fieldRepository;
    private final ProfileFieldValueRepository profileFieldValueRepository;

    public RecommendationServiceImpl(FieldRecommendationRepository fieldRecommendationRepository,
                                     ProfileFieldRecommendationRepository profileFieldRecommendationRepository,
                                     FieldTypeRepository fieldTypeRepository,
                                     FieldRepository fieldRepository,
                                     ProfileFieldValueRepository profileFieldValueRepository) {
        this.fieldRecommendationRepository = fieldRecommendationRepository;
        this.profileFieldRecommendationRepository = profileFieldRecommendationRepository;
        this.fieldTypeRepository = fieldTypeRepository;
        this.fieldRepository = fieldRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
    }

    @Override
    public List<String> getByProfile(Profile profile) {
        List<String> recommendations = new ArrayList<>();

        List<FieldRecommendation> fieldRecommendations =
                fieldRecommendationRepository.findByScientometricSystem(profile.getScientometricSystem());

        for (FieldRecommendation fieldRecommendation : fieldRecommendations) {
            if (fieldRecommendation.getField().getType().getName().equals(FieldTypeName.LABEL))
                continue;

            Optional<ProfileFieldValue> optionalProfileFieldValue =
                    profileFieldValueRepository.findByProfileAndField(
                            profile, fieldRecommendation.getField());

            if (optionalProfileFieldValue.isEmpty()) {
                recommendations.add("Введіть поле: " + fieldRecommendation.getField().getName());
            } else {
                Optional<ProfileFieldRecommendation> optionalProfileFieldRecommendation =
                        profileFieldRecommendationRepository.findByProfileAndField(
                                profile, fieldRecommendation.getField());

                if (optionalProfileFieldRecommendation.isPresent()) {
                    if (!optionalProfileFieldRecommendation.get().isConditionFulfilled())
                        recommendations.add(fieldRecommendation.getText());
                }
            }
        }

        Field labelField = getLabelField();
        Optional<FieldRecommendation> fieldRecommendation = fieldRecommendationRepository
                .findByFieldAndScientometricSystem(labelField,
                        profile.getScientometricSystem());

        if (fieldRecommendation.isPresent()) {
            Optional<ProfileFieldRecommendation> optionalProfileFieldRecommendation =
                    profileFieldRecommendationRepository.findByProfileAndField(
                            profile, labelField);

            if (optionalProfileFieldRecommendation.isEmpty()) {
                recommendations.add("Введіть поле: " + fieldRecommendation.get().getField().getName());
            } else {
                if (!optionalProfileFieldRecommendation.get().isConditionFulfilled())
                    recommendations.add(fieldRecommendation.get().getText());
            }
        }

        return recommendations;
    }

    @Override
    public void updateRecommendations(Profile profile, List<ProfileFieldValue> list, boolean containsLabels) {
        List<ProfileFieldRecommendation> profileFieldRecommendations = new ArrayList<>();
        for (ProfileFieldValue profileFieldValue : list) {
            Optional<FieldRecommendation> fieldRecommendation = fieldRecommendationRepository
                    .findByFieldAndScientometricSystem(profileFieldValue.getField(),
                            profile.getScientometricSystem());

            if (fieldRecommendation.isPresent()) {
                Optional<ProfileFieldRecommendation> optionalProfileFieldRecommendation =
                        profileFieldRecommendationRepository.findByProfileAndField(
                                profileFieldValue.getProfile(), profileFieldValue.getField());

                ProfileFieldRecommendation profileFieldRecommendation;
                if (optionalProfileFieldRecommendation.isEmpty()) {
                    profileFieldRecommendation = new ProfileFieldRecommendation();
                    profileFieldRecommendation.setProfile(profile);
                    profileFieldRecommendation.setField(profileFieldValue.getField());
                } else {
                    profileFieldRecommendation = optionalProfileFieldRecommendation.get();
                }
                profileFieldRecommendation.setConditionFulfilled(
                        checkForMatching(profileFieldValue, fieldRecommendation.get()));

                profileFieldRecommendations.add(profileFieldRecommendation);
            }
        }

        // process label recommendation
        Field labelField = getLabelField();
        Optional<FieldRecommendation> fieldRecommendation = fieldRecommendationRepository
                .findByFieldAndScientometricSystem(labelField,
                        profile.getScientometricSystem());

        if (fieldRecommendation.isPresent()) {
            Optional<ProfileFieldRecommendation> optionalProfileFieldRecommendation =
                    profileFieldRecommendationRepository.findByProfileAndField(
                            profile, labelField);

            ProfileFieldRecommendation profileFieldRecommendation;
            if (optionalProfileFieldRecommendation.isEmpty()) {
                profileFieldRecommendation = new ProfileFieldRecommendation();
                profileFieldRecommendation.setProfile(profile);
                profileFieldRecommendation.setField(labelField);
            } else {
                profileFieldRecommendation = optionalProfileFieldRecommendation.get();
            }

            profileFieldRecommendation.setConditionFulfilled(containsLabels);
            profileFieldRecommendations.add(profileFieldRecommendation);
        }

        profileFieldRecommendationRepository.saveAll(profileFieldRecommendations);
    }

    private boolean checkForMatching(ProfileFieldValue profileFieldValue,
                                     FieldRecommendation fieldRecommendation) {
        switch (fieldRecommendation.getRecommendationType().getName()) {
            case NOT_EMPTY -> {
                if (profileFieldValue.getField().getType().getName() == FieldTypeName.BOOLEAN) {
                    return !profileFieldValue.getValue().equals("false");
                } else {
                    return !profileFieldValue.getValue().isEmpty();
                }
            }
            case MATCH_REGEX -> {
                Pattern pattern = Pattern.compile(fieldRecommendation.getKey());
                Matcher matcher = pattern.matcher(profileFieldValue.getValue());
                return matcher.find();
            }
            case CONTAINS_PHRASE -> {
                return profileFieldValue.getValue().contains(fieldRecommendation.getKey());
            }
            case YEAR_NOT_LESS_THAN_CURRENT_ON -> {
                if (profileFieldValue.getValue().isEmpty())
                    return false;

                int year = Integer.parseInt(fieldRecommendation.getKey());
                int profileYear = Integer.parseInt(profileFieldValue.getValue());
                return LocalDate.now().getYear() - year < profileYear;
            }
        }
        return false;
    }

    private Field getLabelField() {
        FieldType labelType = fieldTypeRepository.findByName(FieldTypeName.LABEL)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.FIELD, EntityName.LABEL));
        return fieldRepository.findByType(labelType)
                .orElseThrow(() -> new EntityNotFoundException(EntityName.FIELD, EntityName.LABEL));
    }
}