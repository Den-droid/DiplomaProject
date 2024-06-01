package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.fields.ProfileFieldDto;

import java.util.List;

public record UpdateProfileDto(List<ProfileFieldDto> fields, List<Integer> labelsIds) {
}
