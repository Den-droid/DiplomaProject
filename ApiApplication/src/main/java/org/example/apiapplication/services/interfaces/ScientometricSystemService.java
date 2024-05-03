package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;

import java.util.List;

public interface ScientometricSystemService {
    List<ScientometricSystemDto> getAll();

    boolean isExtractionRunningById(Integer id);

    boolean isExtractionPossibleById(Integer id);
}
