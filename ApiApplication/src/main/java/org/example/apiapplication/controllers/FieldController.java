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
    public ResponseEntity<?> getFieldTypes() {
        List<FieldTypeDto> fieldTypes = fieldService.getAllFieldTypes();
        return ResponseEntity.ok(fieldTypes);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getFieldById(@PathVariable Integer id) {
        FieldDto fieldDto = fieldService.getById(id);
        return ResponseEntity.ok(fieldDto);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<?> getAllFields(@RequestParam(required = false) Integer currentPage) {
        GetFieldsDto fields;
        if (currentPage == null) {
            fields = fieldService.getAllFields();
        } else {
            fields = fieldService.getAllFields(currentPage);
        }
        return ResponseEntity.ok(fields);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> searchFieldsByName(@RequestParam Integer currentPage,
                                                @RequestParam String name) {
        GetFieldsDto fields = fieldService.searchFieldsByName(currentPage, name);
        return ResponseEntity.ok(fields);
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PostMapping
    public ResponseEntity<?> addField(@RequestBody AddFieldDto addFieldDto) {
        fieldService.add(addFieldDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editField(@PathVariable Integer id,
                                       @RequestBody EditFieldDto editFieldDto) {
        fieldService.edit(id, editFieldDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteField(@PathVariable Integer id,
                                         @RequestBody DeleteFieldDto deleteFieldDto) {
        fieldService.delete(id, deleteFieldDto);
        return ResponseEntity.ok().build();
    }
}
