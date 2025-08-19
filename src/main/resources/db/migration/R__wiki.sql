drop view if exists v_wiki_user;
create view v_wiki_user as
select wu.id,
       wu.vallterra_user_id,
       wu.p_type,
       wu.username,
       wu.password,
       coalesce(array_agg(wur.role) filter (where wur.role is not null), '{}') as roles,
       vu.prefers_dark,
       vu.prefers_large,
       vu.allow_large,
       vu.player_name
from wiki_user wu
         left join vallterra_user vu on wu.vallterra_user_id = vu.id
         left join wiki_user_role wur on wu.id = wur.wiki_user_id
group by wu.id, wu.vallterra_user_id, wu.p_type, wu.username, wu.password,
         vu.prefers_dark, vu.prefers_large, vu.allow_large, vu.player_name;
