package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.fields.FieldDto;
import org.example.apiapplication.dto.fields.FieldTypeDto;
import org.example.apiapplication.services.interfaces.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
public class FieldController {
    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping
    public ResponseEntity<?> getFieldsByScientometricSystemId(
            @RequestParam Integer scientometricSystemId) {
        List<FieldDto> fields = fieldService.getFieldsByScientometricSystemId(scientometricSystemId);
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getFieldTypes() {
        List<FieldTypeDto> fields = fieldService.getFieldTypes();
        return ResponseEntity.ok(fields);
    }
}
