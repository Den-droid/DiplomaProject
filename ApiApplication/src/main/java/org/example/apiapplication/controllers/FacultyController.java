package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.dto.indices.EntityIndicesDto;
import org.example.apiapplication.services.interfaces.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
@CrossOrigin
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<?> getAllFaculties() {
        List<FacultyDto> facultyDtos = facultyService.getAll();
        return ResponseEntity.ok(facultyDtos);
    }

    @GetMapping("/indices")
    public ResponseEntity<?> getIndices(@RequestParam Integer scientometricSystemId) {
        List<EntityIndicesDto> facultiesIndicesDtos =
                facultyService.getFacultiesIndices(scientometricSystemId);
        return ResponseEntity.ok(facultiesIndicesDtos);
    }

    @GetMapping("/{id}/indices")
    public ResponseEntity<?> getIndicesByFaculty(@PathVariable Integer id,
                                                 @RequestParam Integer scientometricSystemId) {
        List<EntityIndicesDto> facultyIndicesDtos =
                facultyService.getChairsIndicesByFaculty(id, scientometricSystemId);
        return ResponseEntity.ok(facultyIndicesDtos);
    }
}
