alter table adventure add instance int not null default 0;
alter table adventure add column slug text;

drop index idx_adventure_quest_id_character_id;
create index idx_adventure_quest_id_instance_character_id on adventure(quest_id, instance, character_id);

create or replace function set_adventure_slug()
returns trigger as $$
begin
  if new.slug is null then
    select case when count = 0 then
        lower(regexp_replace(name, e'\\s+', '-', 'g'))
      else
        concat(lower(regexp_replace(name, e'\\s+', '-', 'g')), '-', count)
      end
      into new.slug
      from quest
     join (select count(*) as count 
            from adventure
            where quest_id = new.quest_id
            and slug is not null) as counts
       on true
     where id = new.quest_id;
  end if;

  return new;
end;
$$ language plpgsql;

create trigger adventure_before_insert
before insert or update on adventure
for each row
execute procedure set_adventure_slug();

-- Update existing adventure
UPDATE adventure
SET slug = null WHERE slug is null;

drop trigger adventure_before_insert on adventure;
create trigger adventure_before_insert
before insert on adventure
for each row
execute procedure set_adventure_slug();

alter table adventure alter column slug set not null;
