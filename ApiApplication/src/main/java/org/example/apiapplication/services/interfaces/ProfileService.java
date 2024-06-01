package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.fields.ProfileFieldDto;
import org.example.apiapplication.dto.labels.LabelDto;
import org.example.apiapplication.dto.profile.*;

import java.util.List;

public interface ProfileService {
    GetProfilesDto getForCurrentUser(Integer scientometricSystemId, Integer page);

    GetProfilesDto searchForCurrentUser(Integer scientometricSystemId,
                                        String fullName, Integer facultyId,
                                        Integer chairId, Integer page);

    List<LabelDto> getLabelsById(Integer profileId);

    List<ProfileFieldDto> getProfileFieldValuesById(Integer profileId);

    boolean canProfileBeCreatedBySystemAndScientist(Integer scientistId,
                                                    Integer scientometricSystemId);

    List<ProfileByLabelDto> getByLabelId(Integer labelId);

    List<ProfileForUserDto> getAll(Integer scientometricSystemId,
                                   Integer chairId);

    void create(CreateProfileDto createProfileDto);

    void update(Integer id, UpdateProfileDto updateProfileDto);

    void deactivate(Integer id);

    void activate(Integer id);

    void markDoubtful(Integer id);

    void unmarkDoubtful(Integer id);
}
