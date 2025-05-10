drop database if exists bookkeeper_gen;
do $$
    begin
        create role bookkeeper login password 'admin';
    exception
        when duplicate_object then
        null; -- ignore if role already exists
    end;
$$;
create database bookkeeper_gen with owner = bookkeeper;