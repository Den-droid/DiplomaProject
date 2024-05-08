package org.example.apiapplication.dto.user;

public record UserDto(Integer id, String email, String fullName,
                      boolean isApproved, boolean isActive, boolean isSignedUp) {
}
