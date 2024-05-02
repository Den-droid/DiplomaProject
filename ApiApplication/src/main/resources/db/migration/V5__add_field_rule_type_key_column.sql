CREATE TABLE field_rule_types
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_field_rule_types PRIMARY KEY (id)
);

ALTER TABLE fields
    ADD field_rule_type_id INTEGER;

ALTER TABLE fields
    ADD key VARCHAR(255);

ALTER TABLE profile_field_values
    ADD key VARCHAR(255);

ALTER TABLE fields
    ADD CONSTRAINT FK_FIELDS_ON_FIELD_RULE_TYPE FOREIGN KEY (field_rule_type_id) REFERENCES field_rule_types (id);