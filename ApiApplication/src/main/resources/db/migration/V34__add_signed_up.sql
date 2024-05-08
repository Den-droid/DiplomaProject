ALTER TABLE users
    ADD is_signed_up BOOLEAN;

ALTER TABLE user_permissions
    DROP COLUMN id;

ALTER TABLE user_permissions
    ALTER COLUMN permission_id SET NOT NULL;

ALTER TABLE user_permissions
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE user_permissions
    ADD CONSTRAINT pk_user_permissions PRIMARY KEY (permission_id, user_id);