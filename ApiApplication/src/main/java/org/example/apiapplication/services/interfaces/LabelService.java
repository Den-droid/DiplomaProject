package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.labels.*;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;

import java.util.List;

public interface LabelService {
    GetLabelsDto getAllLabels(int page);

    GetLabelsDto getAllLabels();

    GetLabelsDto getAllLabelsByName(int page, String name);

    LabelDto getById(Integer id);

    void add(AddLabelDto addLabelDto);

    void update(Integer id, EditLabelDto editLabelDto);

    void delete(Integer id, DeleteLabelDto deleteLabelDto);

    List<Label> getAllByExtraction(List<String> labels);

    void addLabelsToProfile(List<Integer> labelIds, Profile profile);
}
