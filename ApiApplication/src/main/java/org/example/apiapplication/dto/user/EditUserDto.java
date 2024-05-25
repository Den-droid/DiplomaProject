package org.example.apiapplication.dto.user;

import java.util.List;

public record EditUserDto(String fullName, List<Integer> permissionIds) {
}
