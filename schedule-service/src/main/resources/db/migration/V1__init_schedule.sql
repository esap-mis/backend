create table doctor_ref
(
    id bigint not null,
    first_name varchar(255),
    patronymic varchar(100),
    last_name varchar(255),
    specialization varchar(255),
    gender integer,
    clinic_id bigint,
    primary key (id)
);

create table patient_ref
(
    id bigint not null,
    first_name varchar(100),
    patronymic varchar(100),
    last_name varchar(100),
    birth_date date,
    gender integer,
    address varchar(200),
    phone_number varchar(20),
    email varchar(100),
    clinic_id bigint,
    primary key (id)
);

create table schedules
(
    id bigserial not null,
    date date,
    end_doctor_appointment time,
    max_patient_per_day integer not null,
    start_doctor_appointment time,
    doctor_id bigint,
    primary key (id),
    foreign key (doctor_id) references doctor_ref (id)
);

create table appointments
(
    id bigserial not null,
    date date,
    end_time time,
    start_time time,
    status varchar(20) default 'CONFIRMED',
    patient_id bigint,
    doctor_id bigint,
    schedule_id bigint,
    primary key (id),
    foreign key (patient_id) references patient_ref (id),
    foreign key (doctor_id) references doctor_ref (id),
    foreign key (schedule_id) references schedules (id)
);
