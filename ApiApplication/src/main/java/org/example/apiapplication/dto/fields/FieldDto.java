package org.example.apiapplication.dto.fields;

public record FieldDto(Integer id, String name, boolean canBeDeleted, FieldTypeDto fieldType) {
}
