create table clinics
(
    id bigserial not null,
    address varchar(255) not null,
    name varchar(255) not null,
    phone_number varchar(255) not null,
    primary key (id)
);
create table doctors
(
    id bigserial not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    login varchar(255),
    password varchar(255),
    patronymic varchar(100) not null,
    specialization varchar(255) not null,
    primary key (id)
);
create table patients
(
    id bigserial not null,
    address varchar(200),
    birth_date date not null,
    email varchar(100),
    first_name varchar(100),
    last_name varchar(100),
    patronymic varchar(100),
    phone_number varchar(20),
    primary key (id)
);
create table medical_card
(
    id bigserial not null,
    patient_id bigint,
    primary key (id),
    FOREIGN KEY (patient_id) REFERENCES patients (id)
);
create table medical_record
(
    id bigserial not null,
    doctor varchar(255) not null,
    date date not null,
    record varchar(255),
    medical_card_id bigint not null,
    primary key (id),
    FOREIGN KEY (medical_card_id) REFERENCES medical_card (id)
);
create table analyzes
(
    id bigserial not null,
    date timestamp(6),
    name varchar(255),
    result varchar(255),
    medical_record_id bigint,
    primary key (id),
    FOREIGN KEY (medical_record_id) REFERENCES medical_record (id)
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
    FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);
create table appointments
(
    id bigserial not null,
    date date,
    end_time time,
    start_time time,
    patient_id bigint,
    schedule_id bigint,
    primary key (id),
    FOREIGN KEY (patient_id) REFERENCES patients (id),
    FOREIGN KEY (schedule_id) REFERENCES schedules (id)
);
create table role (
    id bigserial not null,
    name varchar(255) not null,
    primary key (id)
);
create table role_doctor (
    id bigserial not null,
    role_id bigint not null,
    doctor_id bigint not null,
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    primary key (id)
);
