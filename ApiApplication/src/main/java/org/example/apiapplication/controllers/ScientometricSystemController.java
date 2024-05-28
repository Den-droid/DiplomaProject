package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientometric_system.ExtractionErrorsDto;
import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scientometricSystems")
@CrossOrigin
public class ScientometricSystemController {
    private final ScientometricSystemService scientometricSystemService;

    public ScientometricSystemController(ScientometricSystemService scientometricSystemService) {
        this.scientometricSystemService = scientometricSystemService;
    }

    @GetMapping
    public ResponseEntity<?> getAllScientometricSystems() {
        List<ScientometricSystemDto> dtos = scientometricSystemService.getAll();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/extraction/isRunning")
    public ResponseEntity<?> getScientometricSystemIsRunning(@PathVariable Integer id) {
        boolean isRunning = scientometricSystemService.isExtractionRunningById(id);
        return ResponseEntity.ok(isRunning);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/extraction/isPossible")
    public ResponseEntity<?> getScientometricSystemIsPossible(@PathVariable Integer id) {
        boolean isPossible = scientometricSystemService.isExtractionPossibleById(id);
        return ResponseEntity.ok(isPossible);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/extraction/errors")
    public ResponseEntity<?> getScientometricSystemExtractionErrors(@PathVariable Integer id) {
        ExtractionErrorsDto extractionErrorsDto = scientometricSystemService.getExtractionErrorsById(id);
        return ResponseEntity.ok(extractionErrorsDto);
    }
}
