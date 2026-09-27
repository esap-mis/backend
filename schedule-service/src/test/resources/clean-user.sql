delete from appointments;
delete from schedules;
delete from patient_ref;
delete from doctor_ref;

alter table if exists schedules alter column id restart with 1;
alter table if exists appointments alter column id restart with 1;
