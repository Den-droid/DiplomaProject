package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.entities.fields.ProfileFieldValue;

import java.util.List;

public interface RecommendationService {
    List<String> getByProfile(Profile profile);

    void updateRecommendations(Profile profile, List<ProfileFieldValue> list, boolean containsLabels);
}
