package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface FacultyService {
    List<FacultyDto> getAll();

    FacultyDto getById(Integer id);

    List<FacultyDto> getByUser(User user);
}
