package org.example.apiapplication.dto.user;

import java.util.List;

public record CreateAdminDto(String email, List<Integer> facultyIds,
                             List<Integer> chairIds, boolean isMainAdmin,
                             List<Integer> permissions) {
}
