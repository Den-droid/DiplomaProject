package org.example.apiapplication.dto.fields;

import org.example.apiapplication.dto.page.PageDto;

import java.util.List;

public record GetFieldsDto(List<FieldDto> fields, PageDto pageDto) {
}
