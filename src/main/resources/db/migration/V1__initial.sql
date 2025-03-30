create table vallterra_user
(
    id       serial primary key,
    p_type   text default 'p'::text,
    username text        not null unique
);

create table casbin_rule
(
    p_type varchar(2) default ''::character varying not null,
    v0     text       default ''::character varying not null,
    v1     text       default ''::character varying not null,
    v2     text       default ''::character varying not null,
    v3     text       default ''::character varying not null,
    v4     text       default ''::character varying not null,
    v5     text       default ''::character varying not null,
    primary key (p_type, v0, v1, v2)
);

create index idx_casbin_rule_v0 on casbin_rule (v0);
create index idx_casbin_rule_v1 on casbin_rule (v1);
create index idx_casbin_rule_v2 on casbin_rule (v2);
create index idx_casbin_rule_v3 on casbin_rule (v3);
create index idx_casbin_rule_v4 on casbin_rule (v4);
create index idx_casbin_rule_v5 on casbin_rule (v5);
create index idx_casbin_rule_p_type on casbin_rule (p_type);

create table character
(
    id                 serial primary key,
    vallterra_user_id            bigint  not null references vallterra_user,
    name               text    not null,
    race               text    not null,
    class              text    not null,
    level              integer not null,
    background         text    not null,
    alignment          text    not null,
    languages          text    not null,
    speed              integer not null,
    tools              text,
    passive_perception integer,
    passive_insight    integer,
    initiative         integer,
    armor_class        integer,
    bio                text,
    points             integer default 0
);

create index idx_character_user_id on character (vallterra_user_id);

create table quest
(
    id       serial primary key,
    name     text not null,
    location text not null,
    notes    text
);

create table adventure
(
    id           serial primary key,
    character_id bigint references character,
    quest_id     bigint references quest,
    unique (character_id, quest_id)
);

create table file
(
    id   serial primary key,
    name text not null,
    path text not null
);

create table comment
(
    id           serial primary key,
    file_id      bigint references file,
    character_id bigint references character,
    comment      text not null,
    created_at   timestamp default now()
);

create table point_reward
(
    id          serial primary key,
    point_cost  integer not null,
    reward      text    not null,
    description text,
    duration    interval,
    xp_gained   integer
);

create index idx_point_cost on point_reward (point_cost);

