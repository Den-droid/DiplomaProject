package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scientists")
@CrossOrigin
public class ScientistController {
    private final ScientistService scientistService;

    public ScientistController(ScientistService scientistService) {
        this.scientistService = scientistService;
    }

    @GetMapping("/not-registered")
    public ResponseEntity<?> getNotRegisteredScientists() {
        List<ScientistPreviewDto> scientists = scientistService.getNotRegisteredScientists();
        return ResponseEntity.ok(scientists);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/accessible-for-current-user")
    public ResponseEntity<?> getForCurrentUser() {
        List<ScientistPreviewDto> scientists = scientistService.getForCurrentUser();
        return ResponseEntity.ok(scientists);
    }
}
