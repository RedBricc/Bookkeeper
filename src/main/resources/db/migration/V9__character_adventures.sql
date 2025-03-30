create table character_adventure (
    id serial primary key,
    character_id int not null references character(id),
    adventure_id int not null references adventure(id),
    notes text not null default '',
    unique (character_id, adventure_id)
);

alter table adventure drop column instance;
alter table adventure drop constraint adventure_character_id_quest_id_key;
alter table adventure drop column character_id;
