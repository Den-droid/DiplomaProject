package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.scientist.EditScientistDto;

public interface ScientistService {
    void edit(Integer id, EditScientistDto editScientistDto);
}
