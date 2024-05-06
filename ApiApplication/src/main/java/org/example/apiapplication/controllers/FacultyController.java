package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.faculties.FacultyDto;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
@CrossOrigin
public class FacultyController {
    private final FacultyService facultyService;
    private final SessionUtil sessionUtil;

    public FacultyController(FacultyService facultyService, SessionUtil sessionUtil) {
        this.facultyService = facultyService;
        this.sessionUtil = sessionUtil;
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

    @GetMapping("/user")
    public ResponseEntity<?> getChairsForUser() {
        User user = sessionUtil.getUserFromSession();
        List<FacultyDto> facultyDtos = facultyService.getByUser(user);
        return ResponseEntity.ok(facultyDtos);
    }
}
