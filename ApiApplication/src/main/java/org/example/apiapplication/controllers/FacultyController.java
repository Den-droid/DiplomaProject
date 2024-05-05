package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.services.interfaces.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacultyById(@PathVariable Integer id) {
        FacultyDto facultyDto = facultyService.getById(id);
        return ResponseEntity.ok(facultyDto);
    }
}
