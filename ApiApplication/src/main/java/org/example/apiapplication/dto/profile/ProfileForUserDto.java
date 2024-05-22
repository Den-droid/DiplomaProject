package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.indices.IndicesDto;

import java.util.List;

public record ProfileForUserDto(String name, IndicesDto indices, List<String> recommendations) {
}
