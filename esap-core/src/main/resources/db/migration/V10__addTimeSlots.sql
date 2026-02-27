create table if not exists time_slots (
    id bigserial not null primary key,
    start_time time not null,
    end_time time not null,
    schedule_id bigint references schedules(id) on delete cascade,
    is_available boolean default TRUE
);

alter table appointments add column time_slot_id bigint references time_slots(id);

alter table schedules drop column start_doctor_appointment;
alter table schedules drop column end_doctor_appointment;
alter table schedules drop column max_patient_per_day;

alter table appointments drop column date;
alter table appointments drop column start_time;
alter table appointments drop column end_time;