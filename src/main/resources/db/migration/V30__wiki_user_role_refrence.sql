alter table public.wiki_user_role
    drop constraint character_role_character_id_fkey;

alter table public.wiki_user_role
    add constraint wiki_user_role_character_id_fkey
        foreign key (wiki_user_id) references public.wiki_user;
