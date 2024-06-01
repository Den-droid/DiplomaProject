package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.labels.*;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;

import java.util.List;

public interface LabelService {
    GetLabelsDto getAll(int page);

    GetLabelsDto getAll();

    GetLabelsDto search(int page, String name);

    LabelDto getById(Integer id);

    void create(CreateLabelDto createLabelDto);

    void update(Integer id, UpdateLabelDto updateLabelDto);

    void delete(Integer id, DeleteLabelDto deleteLabelDto);

    List<Label> getAllByExtraction(List<String> labels);

    void addToProfile(List<Integer> labelIds, Profile profile);
}
