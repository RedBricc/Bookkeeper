alter table character
    add column short_name text;

update character
    set short_name = name;

alter table character
    alter column short_name set not null;
