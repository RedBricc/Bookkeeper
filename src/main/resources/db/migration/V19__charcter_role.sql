create table character_role (
    id serial primary key,
    character_id int not null references character(id),
    role text not null
);

create index idx_character_role_character_id on character_role (character_id);