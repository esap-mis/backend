delete from role_user;
delete from role;
delete from medical_card;
delete from patients;
delete from schedules;
delete from appointments;
delete from doctors;
delete from users;
delete from clinics;

alter table if exists clinics alter column id restart with 1;
alter table if exists users alter column id restart with 1;
alter table if exists doctors alter column id restart with 1;
alter table if exists patients alter column id restart with 1;
alter table if exists medical_card alter column id restart with 1;
alter table if exists schedules alter column id restart with 1;
alter table if exists appointments alter column id restart with 1;