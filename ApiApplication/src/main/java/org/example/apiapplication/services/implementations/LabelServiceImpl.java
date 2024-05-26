package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.labels.*;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.entities.Label;
import org.example.apiapplication.entities.Profile;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.exceptions.label.LabelAlreadyExistsException;
import org.example.apiapplication.repositories.LabelRepository;
import org.example.apiapplication.services.interfaces.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public GetLabelsDto getAllLabels(int page) {
        Page<Label> labelPage = labelRepository.findAll(PageRequest.of(page - 1, 25));

        List<LabelDto> labelDtos = labelPage.getContent().stream()
                .map(x -> new LabelDto(x.getId(), x.getName()))
                .toList();

        return new GetLabelsDto(labelDtos, new PageDto(page, labelPage.getTotalPages()));
    }

    @Override
    public GetLabelsDto getAllLabels() {
        List<Label> labels = new ArrayList<>();

        for (Label label : labelRepository.findAll()) {
            labels.add(label);
        }

        List<LabelDto> labelsDtos = labels.stream()
                .map(x -> new LabelDto(x.getId(), x.getName()))
                .toList();

        return new GetLabelsDto(labelsDtos, new PageDto(1, labels.size()));
    }

    @Override
    public GetLabelsDto searchLabelsByName(int page, String name) {
        Page<Label> labelPage = labelRepository.findByNameContainsIgnoreCase(name.trim(),
                PageRequest.of(page - 1, 5));

        List<LabelDto> labelDtos = labelPage.getContent().stream()
                .map(x -> new LabelDto(x.getId(), x.getName()))
                .toList();

        return new GetLabelsDto(labelDtos, new PageDto(page, labelPage.getTotalPages()));
    }

    @Override
    public LabelDto getById(Integer id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.LABEL, id));

        return new LabelDto(label.getId(), label.getName());
    }

    @Override
    public void add(AddLabelDto addLabelDto) {
        if (labelRepository.findByNameIgnoreCase(addLabelDto.name()).isPresent()) {
            throw new LabelAlreadyExistsException(addLabelDto.name());
        }

        Label label = new Label();
        label.setName(addLabelDto.name());

        labelRepository.save(label);
    }

    @Override
    public void edit(Integer id, EditLabelDto editLabelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.LABEL, id));

        if (labelRepository.findByNameIgnoreCaseAndIdNot(editLabelDto.name(), id).isPresent()) {
            throw new LabelAlreadyExistsException(editLabelDto.name());
        }

        label.setName(editLabelDto.name());

        labelRepository.save(label);
    }

    @Override
    public void delete(Integer id, DeleteLabelDto deleteLabelDto) {
        Label deletedLabel = labelRepository
                .findById(id).orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.LABEL, id));
        Label replacementLabel = labelRepository
                .findById(deleteLabelDto.replacementLabelId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.LABEL,
                        deleteLabelDto.replacementLabelId()));

        List<Profile> labelProfiles = deletedLabel.getProfiles();
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
            Optional<Label> optionalLabel = labelRepository.findByNameIgnoreCase(label);
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
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.LABEL, labelId));
            labels.add(label);
            profile.getLabels().add(label);
        }

        profile.getLabels().retainAll(labels);
    }
}
