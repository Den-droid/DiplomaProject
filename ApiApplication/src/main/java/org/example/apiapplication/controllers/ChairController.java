package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.chairs.ChairDto;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chairs")
@CrossOrigin
public class ChairController {
    private final ChairService chairService;
    private final SessionUtil sessionUtil;

    public ChairController(ChairService chairService,
                           SessionUtil sessionUtil) {
        this.chairService = chairService;
        this.sessionUtil = sessionUtil;
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

    @GetMapping("/user")
    public ResponseEntity<?> getChairsForUser() {
        User user = sessionUtil.getUserFromSession();
        List<ChairDto> chairDtos = chairService.getByUser(user);
        return ResponseEntity.ok(chairDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChairById(@PathVariable Integer id) {
        ChairDto chairDto = chairService.getById(id);
        return ResponseEntity.ok(chairDto);
    }
}
