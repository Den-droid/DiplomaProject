package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.labels.AddLabelDto;
import org.example.apiapplication.dto.labels.DeleteLabelDto;
import org.example.apiapplication.dto.labels.EditLabelDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.services.interfaces.LabelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public ResponseEntity<?> getAllLabels() {
        List<LabelDto> labels = labelService.getAllLabels();
        return ResponseEntity.ok(labels);
    }

    @PostMapping
    public ResponseEntity<?> addLabel(@RequestBody AddLabelDto labelDto) {
        labelService.add(labelDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editLabel(@PathVariable Integer id,
                                       @RequestBody EditLabelDto editLabelDto) {
        labelService.update(id, editLabelDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Integer id,
                                         @RequestBody DeleteLabelDto deleteLabelDto) {
        labelService.delete(id, deleteLabelDto);
        return ResponseEntity.ok().build();
    }
}
