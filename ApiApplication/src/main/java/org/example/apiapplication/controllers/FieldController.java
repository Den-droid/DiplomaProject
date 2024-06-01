package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.fields.*;
import org.example.apiapplication.services.interfaces.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin
public class FieldController {
    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/types")
    public ResponseEntity<?> getTypes() {
        List<FieldTypeDto> fieldTypes = fieldService.getAllFieldTypes();
        return ResponseEntity.ok(fieldTypes);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        FieldDto fieldDto = fieldService.getById(id);
        return ResponseEntity.ok(fieldDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer currentPage) {
        GetFieldsDto fields;
        if (currentPage == null) {
            fields = fieldService.getAll();
        } else {
            fields = fieldService.getAll(currentPage);
        }
        return ResponseEntity.ok(fields);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam Integer currentPage,
                                    @RequestParam String name) {
        GetFieldsDto fields = fieldService.search(currentPage, name);
        return ResponseEntity.ok(fields);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateFieldDto createFieldDto) {
        fieldService.create(createFieldDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody UpdateFieldDto updateFieldDto) {
        fieldService.update(id, updateFieldDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Integer id,
                                    @RequestBody DeleteFieldDto deleteFieldDto) {
        fieldService.delete(id, deleteFieldDto);
        return ResponseEntity.ok().build();
    }
}
