package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;

import java.util.List;

public interface FieldService {
    List<FieldTypeDto> getFieldTypes();

    List<FieldDto> getFieldsByScientometricSystemId(Integer scientometricSystemId);
}
