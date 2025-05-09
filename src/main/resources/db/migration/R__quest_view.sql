drop view if exists v_quest;
create view v_quest as
select q.id,
       q.name,
       q.location,
       q.location_link,
       q.description,
       q.dm_notes,
       q.points,
       q.completed_at,
       q.contact,
       q.contact_link,
       q.difficulty,
       a.count as adventure_count
from quest q
         left join (select quest_id, count(*) as count
                    from adventure
                    group by quest_id) a on q.id = a.quest_id;