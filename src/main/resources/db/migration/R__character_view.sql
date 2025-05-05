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
    c.vallterra_user_id,
    vu.username,
    vu.prefers_dark,
    vu.prefers_large,
    vu.allow_large,
    vu.player_name,
    ca.adventure_count
from character c
join public.vallterra_user vu on c.vallterra_user_id = vu.id
join (
    select ca.character_id, count(ca.id) as adventure_count
    from character_adventure ca
    group by ca.character_id
) ca on ca.character_id = c.id