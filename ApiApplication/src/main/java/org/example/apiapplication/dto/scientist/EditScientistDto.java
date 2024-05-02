package org.example.apiapplication.dto.scientist;

public record EditScientistDto(String fullName, String position,
                               Integer facultyId, Integer chairId) {
}
