package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.*;
import org.example.apiapplication.services.interfaces.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/common-labels")
    public ResponseEntity<?> getByCommonLabel(@RequestParam Integer labelId) {
        List<ProfileByLabelDto> profiles = profileService.getByLabelId(labelId);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam Integer chairId,
                                    @RequestParam Integer scientometricSystemId) {
        List<ProfileForUserDto> profiles =
                profileService.getAll(scientometricSystemId, chairId);
        return ResponseEntity.ok(profiles);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/labels")
    public ResponseEntity<?> getProfileLabels(@PathVariable Integer id) {
        List<LabelDto> labels = profileService.getLabelsById(id);
        return ResponseEntity.ok(labels);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/fields")
    public ResponseEntity<?> getProfileFields(@PathVariable Integer id) {
        List<ProfileFieldDto> profileFields = profileService.getProfileFieldValuesById(id);
        return ResponseEntity.ok(profileFields);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/accessible-for-current-user")
    public ResponseEntity<?> getForCurrentUser(@RequestParam Integer scientometricSystemId,
                                               @RequestParam Integer currentPage) {
        GetProfilesDto profilePreviewDtos = profileService
                .getForCurrentUser(scientometricSystemId, currentPage);
        return ResponseEntity.ok(profilePreviewDtos);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/accessible-for-current-user/search")
    public ResponseEntity<?> searchForCurrentUser(@RequestParam Integer scientometricSystemId,
                                                  @RequestParam String fullName,
                                                  @RequestParam Integer facultyId,
                                                  @RequestParam Integer chairId,
                                                  @RequestParam Integer currentPage) {
        GetProfilesDto profilePreviewDtos = profileService
                .searchForCurrentUser(scientometricSystemId, fullName,
                        facultyId, chairId, currentPage);
        return ResponseEntity.ok(profilePreviewDtos);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProfileDto createProfileDto) {
        profileService.create(createProfileDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody UpdateProfileDto updateProfileDto) {
        profileService.update(id, updateProfileDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/can-create-profile")
    public ResponseEntity<?> canBeAdded(@RequestParam Integer scientistId,
                                        @RequestParam Integer scientometricSystemId) {
        boolean canBeAdded = profileService.canProfileBeCreatedBySystemAndScientist(scientistId,
                scientometricSystemId);
        return ResponseEntity.ok(canBeAdded);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Integer id) {
        profileService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Integer id) {
        profileService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/mark-doubtful")
    public ResponseEntity<?> markDoubtful(@PathVariable Integer id) {
        profileService.markDoubtful(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/unmark-doubtful")
    public ResponseEntity<?> unmarkDoubtful(@PathVariable Integer id) {
        profileService.unmarkDoubtful(id);
        return ResponseEntity.ok().build();
    }
}
