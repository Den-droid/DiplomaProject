package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.dto.scientist.EditScientistDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface ScientistService {
    void edit(Integer id, EditScientistDto editScientistDto);

    List<ScientistPreviewDto> getAllScientistPreview();

    List<ScientistPreviewDto> getAllScientistPreviewByUser(User user);
}
