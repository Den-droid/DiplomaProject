ALTER TABLE users
    ADD full_name VARCHAR(255);

ALTER TABLE users
    ADD is_active BOOLEAN;

ALTER TABLE users
    ADD is_approved BOOLEAN;

ALTER TABLE users
    DROP COLUMN is_verified;