grant usage on schema public to wiki;
grant select on all tables in schema public to wiki;
alter default privileges for user bookkeeper
    in schema public
    grant select on tables to wiki;
