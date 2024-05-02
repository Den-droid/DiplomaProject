package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.AddProfileDto;
import org.example.apiapplication.dto.profile.EditProfileDto;
import org.example.apiapplication.dto.profile.ProfilePreviewDto;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface ProfileService {
    List<ProfilePreviewDto> getByUserAndScientometricSystemId(User user, Integer scientometricSystemId);

    List<LabelDto> getLabelsById(Integer profileId);

    List<ProfileFieldDto> getProfileFieldValuesById(Integer profileId);

    void add(AddProfileDto addProfileDto);

    void edit(Integer id, EditProfileDto editProfileDto);

    void deactivate(Integer id);

    void activate(Integer id);

    void markWorksDoubtful(Integer id);

    void unmarkWorksDoubtful(Integer id);
}
