package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.entities.recommendation.FieldRecommendation;
import org.example.apiapplication.entities.recommendation.ProfileFieldRecommendation;
import org.example.apiapplication.enums.FieldTypeName;
import org.example.apiapplication.repositories.FieldRecommendationRepository;
import org.example.apiapplication.repositories.ProfileFieldRecommendationRepository;
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

    public RecommendationServiceImpl(FieldRecommendationRepository fieldRecommendationRepository,
                                     ProfileFieldRecommendationRepository profileFieldRecommendationRepository) {
        this.fieldRecommendationRepository = fieldRecommendationRepository;
        this.profileFieldRecommendationRepository = profileFieldRecommendationRepository;
    }

    @Override
    public void updateRecommendations(List<ProfileFieldValue> list) {
        List<ProfileFieldRecommendation> profileFieldRecommendations = new ArrayList<>();
        for (ProfileFieldValue profileFieldValue : list) {
            Optional<FieldRecommendation> fieldRecommendation = fieldRecommendationRepository
                    .findByFieldAndScientometricSystem(profileFieldValue.getField(),
                            profileFieldValue.getProfile().getScientometricSystem());

            if (fieldRecommendation.isPresent()) {
                if (profileFieldValue.getProfileFieldRecommendations().isEmpty()) {
                    ProfileFieldRecommendation profileFieldRecommendation = new ProfileFieldRecommendation();
                    profileFieldRecommendation.setProfileFieldValue(profileFieldValue);
                    profileFieldRecommendation.setConditionFulfilled(
                            checkForMatching(profileFieldValue, fieldRecommendation.get()));

                    profileFieldValue.getProfileFieldRecommendations().add(profileFieldRecommendation);
                } else {
                    ProfileFieldRecommendation profileFieldRecommendation =
                            profileFieldValue.getProfileFieldRecommendations().get(0);
                    profileFieldRecommendation.setConditionFulfilled(
                            checkForMatching(profileFieldValue, fieldRecommendation.get())
                    );
                }
                profileFieldRecommendations.add(profileFieldValue
                        .getProfileFieldRecommendations().get(0));
            }
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
                int year = Integer.parseInt(fieldRecommendation.getKey());
                int profileYear = Integer.parseInt(profileFieldValue.getValue());
                return LocalDate.now().getYear() - year < profileYear;
            }
        }
        return false;
    }
}