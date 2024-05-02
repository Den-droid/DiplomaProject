package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.BaseExceptionDto;
import org.example.apiapplication.services.implementations.ScholarExtractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extraction")
public class ExtractionController {
    public final ScholarExtractionService scholarExtractionService;

    public ExtractionController(ScholarExtractionService scholarExtractionService) {
        this.scholarExtractionService = scholarExtractionService;
    }

    @GetMapping("/scholar")
    public ResponseEntity<?> scholarExtraction() {
        try {
            scholarExtractionService.startExtraction();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseExceptionDto(500, e.getMessage()));
        }
    }
}
