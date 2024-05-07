package org.example.apiapplication.dto.roles;

import java.util.List;

public record UpdateDefaultPermissionsDto(Integer roleId, List<Integer> defaultPermissionsIds) {
}
