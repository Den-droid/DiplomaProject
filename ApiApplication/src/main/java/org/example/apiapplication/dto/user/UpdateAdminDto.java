package org.example.apiapplication.dto.user;

import java.util.List;

public record UpdateAdminDto(String fullName, List<Integer> facultyIds,
                             List<Integer> chairIds,
                             List<Integer> permissions) {
}
