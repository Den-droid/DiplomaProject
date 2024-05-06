package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface ChairService {
    List<ChairDto> getAll();

    List<ChairDto> getByFaculty(Integer id);

    List<ChairDto> getByFaculty(Faculty faculty);

    ChairDto getById(Integer id);

    List<ChairDto> getByUser(User user);
}
