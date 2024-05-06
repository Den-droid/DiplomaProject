CREATE TABLE role_default_permissions
(
    permission_id INTEGER NOT NULL,
    role_id       INTEGER NOT NULL,
    CONSTRAINT pk_role_default_permissions PRIMARY KEY (permission_id, role_id)
);

ALTER TABLE role_default_permissions
    ADD CONSTRAINT fk_roldefper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE role_default_permissions
    ADD CONSTRAINT fk_roldefper_on_role FOREIGN KEY (role_id) REFERENCES roles (id);