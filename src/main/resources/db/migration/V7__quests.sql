alter table quest rename column notes to description;
alter table quest alter column description set default '';
alter table quest alter column description set not null;
alter table quest add column dm_notes text not null default '';
alter table quest add column points integer not null default 0;
alter table quest add column completed_at timestamp;

alter table adventure add column notes text not null default '';
alter table adventure add column created_at timestamp not null default now();
alter table adventure add column updated_at timestamp not null default now();

create index idx_adventure_quest_id on adventure(quest_id);
create index idx_adventure_character_id on adventure(character_id);
create index idx_adventure_quest_id_character_id on adventure(quest_id, character_id);

create or replace function log_updated_at() returns trigger as $$
begin
    new.updated_at = now();
    return new;
end;
$$ language plpgsql;

create trigger adventure_updated_at before update on adventure
for each row execute procedure log_updated_at();