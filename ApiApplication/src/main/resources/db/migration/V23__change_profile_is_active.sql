ALTER TABLE profiles
    ADD is_active BOOLEAN;

ALTER TABLE user_roles
    DROP CONSTRAINT pk_user_roles;

ALTER TABLE profiles
    DROP COLUMN is_deactivated;