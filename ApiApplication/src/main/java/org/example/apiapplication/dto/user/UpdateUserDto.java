package org.example.apiapplication.dto.user;

import java.util.List;

public record UpdateUserDto(String fullName, List<Integer> permissionIds) {
}
