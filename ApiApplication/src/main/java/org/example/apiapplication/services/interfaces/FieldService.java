package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.*;

import java.util.List;

public interface FieldService {
    List<FieldTypeDto> getAllFieldTypes();

    GetFieldsDto getAllFields(Integer currentPage);

    GetFieldsDto getAllFields();

    GetFieldsDto searchFieldsByName(Integer currentPage, String name);

    FieldDto getById(Integer id);

    void add(AddFieldDto addFieldDto);

    void edit(Integer id, EditFieldDto editFieldDto);

    void delete(Integer id, DeleteFieldDto deleteFieldDto);
}
