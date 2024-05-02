ALTER TABLE user_permissions
    ADD is_approved BOOLEAN;

ALTER TABLE user_permissions
    ALTER COLUMN is_approved SET NOT NULL;