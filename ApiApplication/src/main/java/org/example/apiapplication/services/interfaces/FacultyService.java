package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.dto.indices.EntityIndicesDto;

import java.util.List;

public interface FacultyService {
    List<FacultyDto> getAll();

    List<EntityIndicesDto> getFacultiesIndices(Integer scientometricSystemId);

    List<EntityIndicesDto> getChairsIndicesByFaculty(Integer facultyId,
                                                     Integer scientometricSystemId);
}
