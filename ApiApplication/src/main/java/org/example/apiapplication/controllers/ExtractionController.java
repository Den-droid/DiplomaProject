package org.example.apiapplication.controllers;

import org.example.apiapplication.services.implementations.ScholarExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extraction")
@CrossOrigin
public class ExtractionController {
    public final ScholarExtractionService scholarExtractionService;

    public ExtractionController(ScholarExtractionService scholarExtractionService) {
        this.scholarExtractionService = scholarExtractionService;
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/scholar")
    public ResponseEntity<?> scholarExtraction() {
        scholarExtractionService.startExtraction();
        return ResponseEntity.ok().build();
    }
}
