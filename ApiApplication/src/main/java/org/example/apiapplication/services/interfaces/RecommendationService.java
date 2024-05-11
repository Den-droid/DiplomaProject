package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.entities.fields.ProfileFieldValue;

import java.util.List;

public interface RecommendationService {
    void updateRecommendations(List<ProfileFieldValue> list);
}
