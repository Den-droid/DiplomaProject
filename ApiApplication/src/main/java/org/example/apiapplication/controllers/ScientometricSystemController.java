package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}/isRunning")
    public ResponseEntity<?> getScientometricSystemIsRunning(@PathVariable Integer id) {
        boolean isRunning = scientometricSystemService.isExtractionRunningById(id);
        return ResponseEntity.ok(isRunning);
    }

    @GetMapping("/{id}/isPossible")
    public ResponseEntity<?> getScientometricSystemIsPossible(@PathVariable Integer id) {
        boolean isPossible = scientometricSystemService.isExtractionPossibleById(id);
        return ResponseEntity.ok(isPossible);
    }
}
