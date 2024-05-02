package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.labels.AddLabelDto;
import org.example.apiapplication.dto.labels.DeleteLabelDto;
import org.example.apiapplication.dto.labels.EditLabelDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotExistsException;
import org.example.apiapplication.exceptions.label.LabelAlreadyExistsException;
import org.example.apiapplication.repositories.LabelRepository;
import org.example.apiapplication.repositories.ProfileRepository;
import org.example.apiapplication.services.interfaces.LabelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final ProfileRepository profileRepository;

    public LabelServiceImpl(LabelRepository labelRepository,
                            ProfileRepository profileRepository) {
        this.labelRepository = labelRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public List<LabelDto> getAllLabels() {
        List<LabelDto> labels = new ArrayList<>();

        for (Label label : labelRepository.findAll()) {
            labels.add(new LabelDto(label.getId(), label.getName()));
        }

        return labels;
    }

    @Override
    public void add(AddLabelDto addLabelDto) {
        if (labelRepository.findByName(addLabelDto.name()).isPresent()) {
            throw new LabelAlreadyExistsException(addLabelDto.name());
        }

        Label label = new Label();
        label.setName(addLabelDto.name());

        labelRepository.save(label);
    }

    @Override
    public void update(Integer id, EditLabelDto editLabelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotExistsException("Label", id));

        if (labelRepository.findByNameAndIdNot(editLabelDto.name(), id).isPresent()) {
            throw new LabelAlreadyExistsException(editLabelDto.name());
        }

        label.setName(editLabelDto.name());

        labelRepository.save(label);
    }

    @Override
    public void delete(Integer id, DeleteLabelDto deleteLabelDto) {
        Label deletedLabel = labelRepository
                .findById(id).orElseThrow(() -> new EntityWithIdNotExistsException("Label", id));
        Label replacementLabel = labelRepository
                .findById(deleteLabelDto.replacementLabelId())
                .orElseThrow(() -> new EntityWithIdNotExistsException("Label",
                        deleteLabelDto.replacementLabelId()));

        Set<Profile> labelProfiles = deletedLabel.getProfiles();
        for (Profile profile : labelProfiles) {
            profile.getLabels().remove(deletedLabel);
            profile.getLabels().add(replacementLabel);
        }

        labelRepository.delete(deletedLabel);
    }

    @Override
    public List<Label> getAllByExtraction(List<String> labels) {
        List<Label> profileLabels = new ArrayList<>();

        for (String label : labels) {
            Optional<Label> optionalLabel = labelRepository.findByName(label);
            if (optionalLabel.isEmpty()) {
                Label newLabel = new Label();
                newLabel.setName(label);

                profileLabels.add(newLabel);
            } else {
                profileLabels.add(optionalLabel.get());
            }
        }

        return profileLabels;
    }

    @Override
    public void addLabelsToProfile(List<Integer> labelIds, Profile profile) {
        List<Label> labels = new ArrayList<>();
        for (Integer labelId : labelIds) {
            Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new EntityWithIdNotExistsException("Label", labelId));
            labels.add(label);
            profile.getLabels().add(label);
        }

        profile.getLabels().retainAll(labels);

        profileRepository.save(profile);
    }
}
