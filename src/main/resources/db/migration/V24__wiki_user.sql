drop view if exists v_character;

alter table vallterra_user
    drop column p_type;

alter table vallterra_user
    drop column username;

create table wiki_user
(
    id                    serial primary key,
    vallterra_user_id     bigint  not null references vallterra_user,
    p_type                text      default 'p'::text,
    username              text not null unique,
    password              text not null
);

create index idx_wiki_user_vallterra_user_id on wiki_user (vallterra_user_id);

alter table character
    add column wiki_user_id bigint references wiki_user;

alter table character
      drop column vallterra_user_id;
