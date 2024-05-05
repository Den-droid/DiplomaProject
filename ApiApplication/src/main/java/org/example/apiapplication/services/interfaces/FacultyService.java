package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.faculties.FacultyDto;

import java.util.List;

public interface FacultyService {
    List<FacultyDto> getAll();

    FacultyDto getById(Integer id);
}
