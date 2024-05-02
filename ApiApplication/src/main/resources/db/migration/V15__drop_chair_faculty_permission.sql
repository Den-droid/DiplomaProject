ALTER TABLE chair_permissions
    DROP CONSTRAINT fk_chair_permissions_on_chair;

ALTER TABLE chair_permissions
    DROP CONSTRAINT fk_chair_permissions_on_permission;

ALTER TABLE chair_permissions
    DROP CONSTRAINT fk_chair_permissions_on_user;

ALTER TABLE faculty_permissions
    DROP CONSTRAINT fk_faculty_permissions_on_faculty;

ALTER TABLE faculty_permissions
    DROP CONSTRAINT fk_faculty_permissions_on_permission;

ALTER TABLE faculty_permissions
    DROP CONSTRAINT fk_faculty_permissions_on_user;

DROP TABLE chair_permissions CASCADE;

DROP TABLE faculty_permissions CASCADE;