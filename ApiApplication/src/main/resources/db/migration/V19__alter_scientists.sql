ALTER TABLE scientists
    ADD faculty_id INTEGER;

ALTER TABLE scientists
    ADD CONSTRAINT FK_SCIENTISTS_ON_FACULTY FOREIGN KEY (faculty_id) REFERENCES faculties (id);