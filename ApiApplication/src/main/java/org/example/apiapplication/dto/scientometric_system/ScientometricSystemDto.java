package org.example.apiapplication.dto.scientometric_system;

import java.time.LocalDate;

public record ScientometricSystemDto(Integer id, String name, LocalDate nextMinImportDate) {
}
