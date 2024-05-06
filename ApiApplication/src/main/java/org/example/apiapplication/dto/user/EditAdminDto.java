package org.example.apiapplication.dto.user;

import java.util.List;

public record EditAdminDto(String fullName, List<Integer> facultyIds,
                           List<Integer> chairIds) {
}
