package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.services.interfaces.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chairs")
@CrossOrigin
public class ChairController {
    private final ChairService chairService;

    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer facultyId) {
        List<ChairDto> chairDtos;

        if (facultyId != null) {
            chairDtos = chairService.getByFaculty(facultyId);
        } else {
            chairDtos = chairService.getAll();
        }

        return ResponseEntity.ok(chairDtos);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/accessible-for-current-user")
    public ResponseEntity<?> getForCurrentUser() {
        List<ChairDto> chairs = chairService.getForCurrentUser();
        return ResponseEntity.ok(chairs);
    }
}
