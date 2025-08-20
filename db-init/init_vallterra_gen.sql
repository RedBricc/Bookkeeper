drop database if exists vallterra_gen;
do $$
    begin
        create role bookkeeper login password 'admin';
    exception
        when duplicate_object then
            null; -- ignore if role already exists
    end;
$$;
do $$
    begin
        create role wiki login password 'admin';
    exception
        when duplicate_object then
            null; -- ignore if role already exists
    end;
$$;
create database vallterra_gen with owner = bookkeeper;