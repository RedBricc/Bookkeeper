drop view if exists v_character;
create view v_character as
select
    c.id,
    c.name,
    c.race,
    c.main_class,
    c.level,
    c.background,
    c.alignment,
    c.languages,
    c.speed,
    c.tools,
    c.passive_perception,
    c.passive_insight,
    c.initiative,
    c.armor_class,
    c.bio,
    c.points,
    c.slug,
    c.image,
    c.xp,
    c.notes,
    c.wiki_user_id,
    wu.vallterra_user_id,
    wu.username,
    vu.player_name,
    ca.adventure_count
from character c
left join public.wiki_user wu on c.wiki_user_id = wu.id
left join public.vallterra_user vu on wu.vallterra_user_id = vu.id
left join (
    select ca.character_id, count(ca.id) as adventure_count
    from character_adventure ca
    group by ca.character_id
) ca on ca.character_id = c.id
order by c.id