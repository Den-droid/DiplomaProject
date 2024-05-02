CREATE TABLE user_chairs
(
    chair_id INTEGER NOT NULL,
    user_id  INTEGER NOT NULL,
    CONSTRAINT pk_user_chairs PRIMARY KEY (chair_id, user_id)
);

CREATE TABLE user_faculties
(
    faculty_id INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    CONSTRAINT pk_user_faculties PRIMARY KEY (faculty_id, user_id)
);

ALTER TABLE user_chairs
    ADD CONSTRAINT fk_usecha_on_chair FOREIGN KEY (chair_id) REFERENCES chairs (id);

ALTER TABLE user_chairs
    ADD CONSTRAINT fk_usecha_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_faculties
    ADD CONSTRAINT fk_usefac_on_faculty FOREIGN KEY (faculty_id) REFERENCES faculties (id);

ALTER TABLE user_faculties
    ADD CONSTRAINT fk_usefac_on_user FOREIGN KEY (user_id) REFERENCES users (id);