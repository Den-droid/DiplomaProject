package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.AddProfileDto;
import org.example.apiapplication.dto.profile.EditProfileDto;
import org.example.apiapplication.dto.profile.GetProfilesDto;
import org.example.apiapplication.dto.profile.ProfilePreviewDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface ProfileService {
    GetProfilesDto getByUserAndScientometricSystemId(User user, Integer scientometricSystemId, Integer page);
    GetProfilesDto searchByUserAndScientometricSystemId(User user, Integer scientometricSystemId, String fullName, Integer facultyId, Integer chairId,Integer page);

    List<LabelDto> getLabelsById(Integer profileId);

    List<ProfileFieldDto> getProfileFieldValuesById(Integer profileId);

    void add(AddProfileDto addProfileDto);

    void edit(Integer id, EditProfileDto editProfileDto);

    void deactivate(Integer id);

    void activate(Integer id);

    void markWorksDoubtful(Integer id);

    void unmarkWorksDoubtful(Integer id);
}
