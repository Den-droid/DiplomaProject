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
    public ResponseEntity<?> getLabelById(@PathVariable Integer id) {
        LabelDto label = labelService.getById(id);
        return ResponseEntity.ok(label);
    }

    @GetMapping
    public ResponseEntity<?> getAllLabels(@RequestParam(required = false) Integer currentPage) {
        GetLabelsDto labels;

        if (currentPage != null) {
            labels = labelService.getAllLabels(currentPage);
        } else {
            labels = labelService.getAllLabels();
        }

        return ResponseEntity.ok(labels);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> searchLabelsByName(@RequestParam String name,
                                                @RequestParam Integer currentPage) {
        GetLabelsDto labels = labelService.searchLabelsByName(currentPage, name);
        return ResponseEntity.ok(labels);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PostMapping
    public ResponseEntity<?> addLabel(@RequestBody AddLabelDto labelDto) {
        labelService.add(labelDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editLabel(@PathVariable Integer id,
                                       @RequestBody EditLabelDto editLabelDto) {
        labelService.edit(id, editLabelDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteLabel(@PathVariable Integer id,
                                         @RequestBody DeleteLabelDto deleteLabelDto) {
        labelService.delete(id, deleteLabelDto);
        return ResponseEntity.ok().build();
    }
}
