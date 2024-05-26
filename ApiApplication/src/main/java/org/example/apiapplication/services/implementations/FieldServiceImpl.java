package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.fields.*;
import org.example.apiapplication.dto.page.PageDto;
import org.example.apiapplication.entities.extraction.FieldExtraction;
import org.example.apiapplication.entities.fields.Field;
import org.example.apiapplication.entities.fields.FieldType;
import org.example.apiapplication.entities.fields.ProfileFieldValue;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.exceptions.field.FieldAlreadyExistsException;
import org.example.apiapplication.exceptions.field.FieldTypesNotMatchException;
import org.example.apiapplication.repositories.FieldRepository;
import org.example.apiapplication.repositories.FieldTypeRepository;
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

    public FieldServiceImpl(FieldTypeRepository fieldTypeRepository,
                            FieldRepository fieldRepository) {
        this.fieldTypeRepository = fieldTypeRepository;
        this.fieldRepository = fieldRepository;
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
    public GetFieldsDto getAllFields(Integer currentPage) {
        Page<Field> fieldsPage = fieldRepository
                .findAll(PageRequest.of(currentPage - 1, 25));

        List<FieldDto> fields = fieldsPage.getContent().stream()
                .map(this::getFieldDto)
                .toList();

        return new GetFieldsDto(fields, new PageDto(currentPage, fieldsPage.getTotalPages()));
    }

    @Override
    public GetFieldsDto getAllFields() {
        List<FieldDto> fields = new ArrayList<>();

        for (Field field : fieldRepository.findAll()) {
            fields.add(getFieldDto(field));
        }

        return new GetFieldsDto(fields, new PageDto(1, fields.size()));
    }

    @Override
    public GetFieldsDto searchFieldsByName(Integer currentPage, String name) {
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
    public void add(AddFieldDto addFieldDto) {
        if (fieldRepository.findByNameIgnoreCase(addFieldDto.name()).isPresent()) {
            throw new FieldAlreadyExistsException(addFieldDto.name());
        }

        Field field = new Field();
        field.setName(addFieldDto.name());

        FieldType fieldType = fieldTypeRepository.findById(addFieldDto.typeId())
                .orElseThrow(() ->
                        new EntityWithIdNotFoundException(EntityName.FIELD_TYPE, addFieldDto.typeId()));
        field.setType(fieldType);

        fieldRepository.save(field);
    }

    @Override
    public void edit(Integer id, EditFieldDto editFieldDto) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, id));

        if (fieldRepository.findByNameIgnoreCaseAndIdNot(editFieldDto.name(), id).isPresent())
            throw new FieldAlreadyExistsException(editFieldDto.name());

        field.setName(editFieldDto.name());

        fieldRepository.save(field);
    }

    @Override
    public void delete(Integer id, DeleteFieldDto deleteFieldDto) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD, id));

        Field replacementField = fieldRepository.findById(deleteFieldDto.replacementFieldId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FIELD,
                        deleteFieldDto.replacementFieldId()));

        if (!field.getType().getName().name()
                .equals(replacementField.getType().getName().name())) {
            throw new FieldTypesNotMatchException();
        }

        List<ProfileFieldValue> profileFieldValues = field.getProfileFieldValues();
        for (ProfileFieldValue profileFieldValue : profileFieldValues) {
            profileFieldValue.setField(replacementField);
        }

        List<FieldExtraction> fieldExtractions = field.getFieldExtractions();
        fieldExtractions.clear();

        fieldRepository.delete(field);
    }

    private FieldDto getFieldDto(Field field) {
        FieldType fieldType = field.getType();

        return new FieldDto(field.getId(), field.getName(),
                new FieldTypeDto(fieldType.getId(), fieldType.getName().name()));
    }
}
