insert into roles(name)
values ('MAIN_ADMIN'),
	('CHAIR_ADMIN'),
	('FACULTY_ADMIN'),
    ('USER');

insert into permissions(name)
values ('ADD_PROFILES'),
       ('EDIT_PROFILES'),
       ('DEACTIVATE_PROFILES'),
	   ('DEACTIVATE_USERS');

insert into scientometric_systems (name, next_min_import_date, profile_import_periodicity)
values ('SCHOLAR', current_date, 3);