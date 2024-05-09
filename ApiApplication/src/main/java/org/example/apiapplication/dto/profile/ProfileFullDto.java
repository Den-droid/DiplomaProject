package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;

import java.util.List;

public record ProfileFullDto(Integer id, boolean areWorksDoubtful,
                             boolean isActive, List<ProfileFieldDto> fields, List<LabelDto> labels) {
}
