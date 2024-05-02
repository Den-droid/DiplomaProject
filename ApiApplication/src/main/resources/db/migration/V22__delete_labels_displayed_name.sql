ALTER TABLE labels
    ADD name VARCHAR(255);

ALTER TABLE labels
    DROP COLUMN displayed_name;

ALTER TABLE labels
    DROP COLUMN original_name;