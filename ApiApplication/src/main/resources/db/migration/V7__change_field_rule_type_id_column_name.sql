ALTER TABLE fields
    DROP CONSTRAINT fk_fields_on_field_rule_type;

ALTER TABLE fields
    ADD rule_type_id INTEGER;

ALTER TABLE fields
    ADD CONSTRAINT FK_FIELDS_ON_RULE_TYPE FOREIGN KEY (rule_type_id) REFERENCES field_rule_types (id);

ALTER TABLE fields
    DROP COLUMN field_rule_type_id;