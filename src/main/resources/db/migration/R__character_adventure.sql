drop view if exists v_character_adventure;
create view v_character_adventure as
select ca.id,
       ca.character_id,
       ca.adventure_id,
       ca.notes,
       ca.in_party,
       a.quest_id,
       a.notes as adventure_notes,
       a.created_at,
       a.updated_at,
       a.slug
from character_adventure ca
         join adventure a on ca.adventure_id = a.id