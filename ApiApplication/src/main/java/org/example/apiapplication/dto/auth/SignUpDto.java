package org.example.apiapplication.dto.auth;

public record SignUpDto(String fullName, String email, String password, Integer scientistId) {
}
