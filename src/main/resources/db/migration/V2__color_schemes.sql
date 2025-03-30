alter table vallterra_user add column prefers_dark boolean not null default true;

insert into casbin_rule values ('p', 'player', '/settings', 'view');
insert into casbin_rule values ('p', 'player', '/settings/update', 'edit');

insert into casbin_rule values ('p', 'admin', '/settings', 'view');
insert into casbin_rule values ('p', 'admin', '/settings/update', 'edit');
