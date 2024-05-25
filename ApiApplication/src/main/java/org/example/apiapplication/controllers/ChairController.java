package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.services.interfaces.ChairService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAllChairs(@RequestParam(required = false) Integer facultyId) {
        List<ChairDto> chairDtos;

        if (facultyId != null) {
            chairDtos = chairService.getByFaculty(facultyId);
        } else {
            chairDtos = chairService.getAll();
        }

        return ResponseEntity.ok(chairDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChairById(@PathVariable Integer id) {
        ChairDto chairDto = chairService.getById(id);
        return ResponseEntity.ok(chairDto);
    }
}
