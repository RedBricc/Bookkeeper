alter table character_role rename to wiki_user_role;
alter table wiki_user_role
    rename column character_id to wiki_user_id;
