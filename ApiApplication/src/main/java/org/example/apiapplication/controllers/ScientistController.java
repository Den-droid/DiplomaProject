package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientist.ScientistPreviewDto;
import org.example.apiapplication.dto.scientist.EditScientistDto;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scientists")
@CrossOrigin
public class ScientistController {
    private final ScientistService scientistService;

    public ScientistController(ScientistService scientistService) {
        this.scientistService = scientistService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editScientist(@PathVariable Integer id,
                                           @RequestBody EditScientistDto editScientistDto) {
        scientistService.edit(id, editScientistDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preview")
    public ResponseEntity<?> getAllScientistPreview() {
        List<ScientistPreviewDto> scientists = scientistService.getAllScientistPreview();
        return ResponseEntity.ok(scientists);
    }
}
