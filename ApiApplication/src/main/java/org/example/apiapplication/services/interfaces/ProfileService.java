package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.*;
import org.example.apiapplication.entities.user.User;

import java.util.List;

public interface ProfileService {
    GetProfilesDto getByUserAndScientometricSystemId(User user, Integer scientometricSystemId, Integer page);

    GetProfilesDto searchByUserAndScientometricSystemId(User user, Integer scientometricSystemId,
                                                        String fullName, Integer facultyId, Integer chairId, Integer page);

    List<LabelDto> getLabelsById(Integer profileId);

    List<ProfileFieldDto> getProfileFieldValuesById(Integer profileId);

    ProfileFullDto getProfileFullById(Integer id);

    boolean canProfileBeAddedToSystemAndScientist(Integer scientistId, Integer scientometricSystemId);

    List<ProfileByLabelDto> getProfilesByLabelId(Integer labelId);

    List<ProfileForUserDto> getProfilesForUser(Integer scientometricSystemId, Integer chairId);

    void add(AddProfileDto addProfileDto);

    void edit(Integer id, EditProfileDto editProfileDto);

    void deactivate(Integer id);

    void activate(Integer id);

    void markWorksDoubtful(Integer id);

    void unmarkWorksDoubtful(Integer id);
}
