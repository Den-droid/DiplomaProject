package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.scientist.EditScientistDto;
import org.example.apiapplication.services.interfaces.ScientistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scientists")
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
}
