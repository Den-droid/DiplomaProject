package org.example.apiapplication.dto.user;

import org.example.apiapplication.dto.page.PageDto;

import java.util.List;

public record GetUsersDto(List<UserDto> users, PageDto pageDto) {
}
