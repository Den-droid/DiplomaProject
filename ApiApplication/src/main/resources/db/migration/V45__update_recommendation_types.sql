update recommendation_types
set name = 'CONTAINS_PHRASE'
where id = 1;

update recommendation_types
set name = 'MATCH_REGEX'
where id = 2;

update recommendation_types
set name = 'NOT_EMPTY'
where id = 3;

update recommendation_types
set name = 'YEAR_NOT_LESS_THAN_CURRENT_ON'
where id = 4;