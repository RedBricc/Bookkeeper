drop view if exists v_adventure;
create view v_adventure as
select a.id,
       a.quest_id,
       a.notes,
       a.created_at,
       a.updated_at,
       a.slug,
       a.map    as map_path,
       q.name   as quest_name,
       ca.count as party_size
from adventure a
         join quest q on a.quest_id = q.id
         left join (select adventure_id, count(*) as count
                    from character_adventure
                    where in_party = true
                    group by adventure_id) ca on a.id = ca.adventure_id;
