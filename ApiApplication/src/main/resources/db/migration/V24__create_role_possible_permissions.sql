CREATE TABLE role_possible_permissions
(
    permission_id INTEGER NOT NULL,
    role_id       INTEGER NOT NULL,
    CONSTRAINT pk_role_possible_permissions PRIMARY KEY (permission_id, role_id)
);

ALTER TABLE role_possible_permissions
    ADD CONSTRAINT fk_rolposper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE role_possible_permissions
    ADD CONSTRAINT fk_rolposper_on_role FOREIGN KEY (role_id) REFERENCES roles (id);