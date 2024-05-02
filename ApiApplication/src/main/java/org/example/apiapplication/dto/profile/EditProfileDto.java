package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.fields.ProfileFieldDto;

import java.util.List;

public record EditProfileDto(Integer profileId, List<ProfileFieldDto> fields, List<Integer> labelsId) {
}
