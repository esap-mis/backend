alter table appointments add doctor_id bigint;
alter table appointments add constraint doctor_fk foreign key (doctor_id) references doctors(id);