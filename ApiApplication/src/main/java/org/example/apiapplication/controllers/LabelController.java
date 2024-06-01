package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.labels.*;
import org.example.apiapplication.services.interfaces.LabelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/labels")
@CrossOrigin
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        LabelDto label = labelService.getById(id);
        return ResponseEntity.ok(label);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer currentPage) {
        GetLabelsDto labels;

        if (currentPage != null) {
            labels = labelService.getAll(currentPage);
        } else {
            labels = labelService.getAll();
        }

        return ResponseEntity.ok(labels);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name,
                                    @RequestParam Integer currentPage) {
        GetLabelsDto labels = labelService.search(currentPage, name);
        return ResponseEntity.ok(labels);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateLabelDto labelDto) {
        labelService.create(labelDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody UpdateLabelDto updateLabelDto) {
        labelService.update(id, updateLabelDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    @RequestBody DeleteLabelDto deleteLabelDto) {
        labelService.delete(id, deleteLabelDto);
        return ResponseEntity.ok().build();
    }
}
