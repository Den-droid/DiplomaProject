package org.example.apiapplication.dto.labels;

import org.example.apiapplication.dto.page.PageDto;

import java.util.List;

public record GetLabelsDto(List<LabelDto> labels, PageDto pageDto) {
}
