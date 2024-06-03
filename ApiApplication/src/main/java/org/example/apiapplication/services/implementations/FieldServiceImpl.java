package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.fields.*;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldType;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.exceptions.field.FieldAlreadyExistsException;
import org.example.apiapplication.exceptions.field.FieldCannotBeDeletedException;
import org.example.apiapplication.exceptions.field.FieldTypesNotMatchException;
import org.example.apiapplication.repositories.FieldRepository;
import org.example.apiapplication.repositories.FieldTypeRepository;
import org.example.apiapplication.repositories.ProfileFieldValueRepository;
import org.example.apiapplication.services.interfaces.FieldService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FieldServiceImpl implements FieldService {
    private final FieldTypeRepository fieldTypeRepository;
    private final FieldRepository fieldRepository;
    private final ProfileFieldValueRepository profileFieldValueRepository;

    public FieldServiceImpl(FieldTypeRepository fieldTypeRepository,
                            FieldRepository fieldRepository,
                            ProfileFieldValueRepository profileFieldValueRepository) {
        this.fieldTypeRepository = fieldTypeRepository;
        this.fieldRepository = fieldRepository;
        this.profileFieldValueRepository = profileFieldValueRepository;
    }

    @Override
    public List<FieldTypeDto> getAllFieldTypes() {
        List<FieldType> fieldTypes = new ArrayList<>();

        for (FieldType fieldType : fieldTypeRepository.findAll()) {
            fieldTypes.add(fieldType);
        }

        return fieldTypes.stream()
                .map(x -> new FieldTypeDto(x.getId(), x.getName().name()))
                .toList();
    }

    @Override
    public GetFieldsDto getAll(Integer currentPage) {
        Page<Field> fieldsPage = fieldRepository
                .findAll(PageRequest.of(currentPage - 1, 25));

        List<FieldDto> fields = fieldsPage.getContent().stream()
                .map(this::getFieldDto)
                .toList();

        return new GetFieldsDto(fields, new PageDto(currentPage, fieldsPage.getTotalPages()));
    }

    @Override
    public GetFieldsDto getAll() {
        List<FieldDto> fields = new ArrayList<>();

        for (Field field : fieldRepository.findAll()) {
            fields.add(getFieldDto(field));
        }

        return new GetFieldsDto(fields, new PageDto(1, fields.size()));
    }

    @Override
    public GetFieldsDto search(Integer currentPage, String name) {
        Page<Field> fieldPage = fieldRepository.findAllByNameContainsIgnoreCase(name,
                PageRequest.of(currentPage - 1, 25));

        List<FieldDto> fields = fieldPage.getContent().stream()
                .map(this::getFieldDto)
                .toList();

        return new GetFieldsDto(fields, new PageDto(currentPage, fieldPage.getTotalPages()));
    }

    @Override
    public FieldDto getById(Integer id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, id));

        return getFieldDto(field);
    }

    @Override
    public void create(CreateFieldDto createFieldDto) {
        if (fieldRepository.findByNameIgnoreCase(createFieldDto.name()).isPresent()) {
            throw new FieldAlreadyExistsException(createFieldDto.name());
        }

        Field field = new Field();
        field.setName(createFieldDto.name());

        FieldType fieldType = fieldTypeRepository.findById(createFieldDto.typeId())
                .orElseThrow(() ->
                        new EntityWithIdNotFoundException(EntityName.FIELD_TYPE, createFieldDto.typeId()));
        field.setType(fieldType);

        fieldRepository.save(field);
    }

    @Override
    public void update(Integer id, UpdateFieldDto updateFieldDto) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, id));

        if (fieldRepository.findByNameIgnoreCaseAndIdNot(updateFieldDto.name(), id).isPresent())
            throw new FieldAlreadyExistsException(updateFieldDto.name());

        field.setName(updateFieldDto.name());

        fieldRepository.save(field);
    }

    @Override
    public void delete(Integer id, DeleteFieldDto deleteFieldDto) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, id));

        if (!canBeDeleted(field)) {
            throw new FieldCannotBeDeletedException(id);
        }

        Field replacementField = fieldRepository.findById(deleteFieldDto.replacementFieldId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD,
                        deleteFieldDto.replacementFieldId()));

        if (!field.getType().getName().name()
                .equals(replacementField.getType().getName().name())) {
            throw new FieldTypesNotMatchException();
        }

        List<ProfileFieldValue> profileFieldValues = field.getProfileFieldValues();
        List<ProfileFieldValue> profileFieldValuesToDelete = new ArrayList<>();

        for (ProfileFieldValue profileFieldValue : profileFieldValues) {
            if (profileFieldValue.getProfile().getProfileFieldValues()
                    .stream()
                    .noneMatch(x -> x.getField().equals(replacementField)))
                profileFieldValue.setField(replacementField);
            else {
                profileFieldValuesToDelete.add(profileFieldValue);
            }
        }

        profileFieldValueRepository.saveAll(profileFieldValues);
        profileFieldValueRepository.deleteAll(profileFieldValuesToDelete);
        fieldRepository.delete(field);
    }

    private FieldDto getFieldDto(Field field) {
        FieldType fieldType = field.getType();

        return new FieldDto(field.getId(), field.getName(), canBeDeleted(field),
                new FieldTypeDto(fieldType.getId(), fieldType.getName().name()));
    }

    @Override
    public boolean canBeDeleted(Field field) {
        return field.getFieldExtractions().isEmpty() &&
                field.getFieldRecommendations().isEmpty();
    }

    @Override
    public boolean canBeDeleted(Integer fieldId) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, fieldId));

        return canBeDeleted(field);
    }
}
