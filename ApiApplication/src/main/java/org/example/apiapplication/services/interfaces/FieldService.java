package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.*;
import org.example.apiapplication.entities.fields.Field;

import java.util.List;

public interface FieldService {
    List<FieldTypeDto> getAllFieldTypes();

    GetFieldsDto getAll(Integer currentPage);

    GetFieldsDto getAll();

    GetFieldsDto search(Integer currentPage, String name);

    FieldDto getById(Integer id);

    boolean canBeDeleted(Field field);

    boolean canBeDeleted(Integer fieldId);

    void create(CreateFieldDto createFieldDto);

    void update(Integer id, UpdateFieldDto updateFieldDto);

    void delete(Integer id, DeleteFieldDto deleteFieldDto);
}
