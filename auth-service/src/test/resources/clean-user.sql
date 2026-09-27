delete from role_user;
delete from patients;
delete from doctors;
delete from users;
delete from clinics;

alter table if exists clinics alter column id restart with 1;
alter table if exists users alter column id restart with 1;
alter table if exists doctors alter column id restart with 1;
alter table if exists patients alter column id restart with 1;
