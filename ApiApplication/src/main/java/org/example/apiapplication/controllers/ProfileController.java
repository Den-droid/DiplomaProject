package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.AddProfileDto;
import org.example.apiapplication.dto.profile.EditProfileDto;
import org.example.apiapplication.dto.profile.ProfilePreviewDto;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
import org.example.apiapplication.services.interfaces.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final SessionUtil sessionUtil;

    public ProfileController(ProfileService profileService,
                             SessionUtil sessionUtil) {
        this.profileService = profileService;
        this.sessionUtil = sessionUtil;
    }

    @GetMapping("/{id}/labels")
    public ResponseEntity<?> getProfileLabels(@PathVariable Integer id) {
        List<LabelDto> labels = profileService.getLabelsById(id);
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<?> getProfileFields(@PathVariable Integer id) {
        List<ProfileFieldDto> profileFields = profileService.getProfileFieldValuesById(id);
        return ResponseEntity.ok(profileFields);
    }

    @GetMapping
    public ResponseEntity<?> getByUserAndScientometricSystem(@RequestParam Integer scientometricSystemId) {
        User user = sessionUtil.getUserFromSession();
        List<ProfilePreviewDto> profilePreviewDtos = profileService
                .getByUserAndScientometricSystemId(user, scientometricSystemId);
        return ResponseEntity.ok(profilePreviewDtos);
    }

    @PostMapping
    public ResponseEntity<?> addProfile(@RequestBody AddProfileDto addProfileDto) {
        profileService.add(addProfileDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProfile(@PathVariable Integer id,
                                         @RequestBody EditProfileDto editProfileDto) {
        profileService.edit(id, editProfileDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateProfile(@PathVariable Integer id) {
        profileService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateProfile(@PathVariable Integer id) {
        profileService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/markDoubtful")
    public ResponseEntity<?> markDoubtful(@PathVariable Integer id) {
        profileService.markWorksDoubtful(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unmarkDoubtful")
    public ResponseEntity<?> unmarkDoubtful(@PathVariable Integer id) {
        profileService.unmarkWorksDoubtful(id);
        return ResponseEntity.ok().build();
    }
}
