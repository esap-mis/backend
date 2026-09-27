delete from analyzes;
delete from medical_record;
delete from medical_card;
delete from patient_ref;
delete from doctor_ref;

alter table if exists medical_card alter column id restart with 1;
alter table if exists medical_record alter column id restart with 1;
alter table if exists analyzes alter column id restart with 1;
