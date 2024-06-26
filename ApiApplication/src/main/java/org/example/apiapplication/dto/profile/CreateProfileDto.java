package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.fields.ProfileFieldDto;

import java.util.List;

public record CreateProfileDto(Integer scientistId, Integer scientometricSystemId,
                               List<ProfileFieldDto> profileFields, List<Integer> labelsIds) {
}
