create table bookkeeper_user (
    id serial primary key,
    valleterra_user_id bigint references vallterra_user unique,
    username text        not null unique,
    password text        not null
);

create index idx_bookkeeper_user_valleterra_user_id on bookkeeper_user (valleterra_user_id);

create table bookkeeper_user_role (
    id serial primary key,
    bookkeeper_user_id int references bookkeeper_user,
    role text not null
);

create index idx_bookkeeper_user_role_bookkeeper_user_id on bookkeeper_user_role (bookkeeper_user_id);
create index idx_bookkeeper_user_role_bookkeeper_role_id on bookkeeper_user_role (role);

alter table bookkeeper_user_role add constraint unique_bookkeeper_user_role unique (bookkeeper_user_id, role);
