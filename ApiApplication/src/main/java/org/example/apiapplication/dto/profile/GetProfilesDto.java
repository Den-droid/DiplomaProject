package org.example.apiapplication.dto.profile;

import org.example.apiapplication.dto.page.PageDto;

import java.util.List;

public record GetProfilesDto (List<ProfilePreviewDto> profiles, PageDto pageDto){
}
