ALTER TABLE fields
    DROP CONSTRAINT fk_fields_on_rule_type;

ALTER TABLE fields
    DROP CONSTRAINT fk_fields_on_scientometric_system;

CREATE TABLE field_extraction
(
    id                      INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    key                     VARCHAR(255),
    rule                    VARCHAR(255),
    scientometric_system_id INTEGER,
    field_id                INTEGER,
    rule_type_id            INTEGER,
    CONSTRAINT pk_fieldextraction PRIMARY KEY (id)
);

ALTER TABLE field_extraction
    ADD CONSTRAINT FK_FIELDEXTRACTION_ON_FIELD FOREIGN KEY (field_id) REFERENCES fields (id);

ALTER TABLE field_extraction
    ADD CONSTRAINT FK_FIELDEXTRACTION_ON_RULE_TYPE FOREIGN KEY (rule_type_id) REFERENCES field_rule_types (id);

ALTER TABLE field_extraction
    ADD CONSTRAINT FK_FIELDEXTRACTION_ON_SCIENTOMETRIC_SYSTEM FOREIGN KEY (scientometric_system_id) REFERENCES scientometric_systems (id);

ALTER TABLE fields
    DROP COLUMN key;

ALTER TABLE fields
    DROP COLUMN rule;

ALTER TABLE fields
    DROP COLUMN rule_type_id;

ALTER TABLE fields
    DROP COLUMN scientometric_system_id;