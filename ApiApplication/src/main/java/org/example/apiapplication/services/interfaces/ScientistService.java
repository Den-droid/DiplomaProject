package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.scientist.ScientistPreviewDto;

import java.util.List;

public interface ScientistService {
    List<ScientistPreviewDto> getNotRegisteredScientists();

    List<ScientistPreviewDto> getForCurrentUser();
}
