package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.chairs.ChairDto;

import java.util.List;

public interface ChairService {
    List<ChairDto> getAll();

    List<ChairDto> getByFaculty(Integer facultyId);

    List<ChairDto> getForCurrentUser();
}
