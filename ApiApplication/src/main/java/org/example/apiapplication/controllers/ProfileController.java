package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.*;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.security.utils.SessionUtil;
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
    private final SessionUtil sessionUtil;

    public ProfileController(ProfileService profileService,
                             SessionUtil sessionUtil) {
        this.profileService = profileService;
        this.sessionUtil = sessionUtil;
    }

    @GetMapping("/commonLabels")
    public ResponseEntity<?> getProfilesByCommonLabel(@RequestParam Integer labelId) {
        List<ProfileByLabelDto> profiles = profileService.getProfilesByLabelId(labelId);
        return ResponseEntity.ok(profiles);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/forUser")
    public ResponseEntity<?> getProfilesForUser(@RequestParam Integer chairId,
                                                @RequestParam Integer scientometricSystemId) {
        List<ProfileForUserDto> profiles =
                profileService.getProfilesForUser(scientometricSystemId, chairId);
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
    @GetMapping
    public ResponseEntity<?> getByUserAndScientometricSystem(@RequestParam Integer scientometricSystemId,
                                                             @RequestParam Integer currentPage) {
        User user = sessionUtil.getUserFromSession();
        GetProfilesDto profilePreviewDtos = profileService
                .getByUserAndScientometricSystemId(user, scientometricSystemId, currentPage);
        return ResponseEntity.ok(profilePreviewDtos);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/search")
    public ResponseEntity<?> searchByUserAndOtherParams(@RequestParam Integer scientometricSystemId,
                                                        @RequestParam String fullName,
                                                        @RequestParam Integer facultyId,
                                                        @RequestParam Integer chairId,
                                                        @RequestParam Integer currentPage) {
        User user = sessionUtil.getUserFromSession();
        GetProfilesDto profilePreviewDtos = profileService
                .searchByUserAndScientometricSystemId(user, scientometricSystemId, fullName,
                        facultyId, chairId, currentPage);
        return ResponseEntity.ok(profilePreviewDtos);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<?> addProfile(@RequestBody AddProfileDto addProfileDto) {
        profileService.add(addProfileDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editProfile(@PathVariable Integer id,
                                         @RequestBody EditProfileDto editProfileDto) {
        profileService.edit(id, editProfileDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/canAddProfile")
    public ResponseEntity<?> canBeAdded(@RequestParam Integer scientistId,
                                        @RequestParam Integer scientometricSystemId) {
        boolean canBeAdded = profileService.canProfileBeAddedToSystemAndScientist(scientistId,
                scientometricSystemId);
        return ResponseEntity.ok(canBeAdded);
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activateProfile(@PathVariable Integer id) {
        profileService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'FACULTY_ADMIN', 'CHAIR_ADMIN', 'USER')")
    @GetMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateProfile(@PathVariable Integer id) {
        profileService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/markDoubtful")
    public ResponseEntity<?> markDoubtful(@PathVariable Integer id) {
        profileService.markWorksDoubtful(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MAIN_ADMIN')")
    @GetMapping("/{id}/unmarkDoubtful")
    public ResponseEntity<?> unmarkDoubtful(@PathVariable Integer id) {
        profileService.unmarkWorksDoubtful(id);
        return ResponseEntity.ok().build();
    }
}
