package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientometric_system.ScientometricSystemDto;
import org.example.apiapplication.services.interfaces.ScientometricSystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scientometricSystems")
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
}
