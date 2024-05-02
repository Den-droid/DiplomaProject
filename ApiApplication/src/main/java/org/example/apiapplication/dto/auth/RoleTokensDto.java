package org.example.apiapplication.dto.auth;

import java.util.List;

public record RoleTokensDto(List<String> roles, TokensDto tokensDto) {
}
