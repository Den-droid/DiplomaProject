ALTER TABLE profile_field_recommendations
    DROP CONSTRAINT fk_profile_field_recommendations_on_profile_field_value;

ALTER TABLE profile_field_recommendations
    ADD field_id INTEGER;

ALTER TABLE profile_field_recommendations
    ADD profile_id INTEGER;

ALTER TABLE profile_field_recommendations
    ADD CONSTRAINT FK_PROFILE_FIELD_RECOMMENDATIONS_ON_FIELD FOREIGN KEY (field_id) REFERENCES fields (id);

ALTER TABLE profile_field_recommendations
    ADD CONSTRAINT FK_PROFILE_FIELD_RECOMMENDATIONS_ON_PROFILE FOREIGN KEY (profile_id) REFERENCES profiles (id);

ALTER TABLE profile_field_recommendations
    DROP COLUMN profile_field_value_id;