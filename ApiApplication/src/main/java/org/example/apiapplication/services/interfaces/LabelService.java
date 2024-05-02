package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.labels.AddLabelDto;
import org.example.apiapplication.dto.labels.DeleteLabelDto;
import org.example.apiapplication.dto.labels.EditLabelDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;

import java.util.List;

public interface LabelService {
    List<LabelDto> getAllLabels();

    void add(AddLabelDto addLabelDto);

    void update(Integer id, EditLabelDto editLabelDto);

    void delete(Integer id, DeleteLabelDto deleteLabelDto);

    List<Label> getAllByExtraction(List<String> labels);

    void addLabelsToProfile(List<Integer> labelIds, Profile profile);
}
