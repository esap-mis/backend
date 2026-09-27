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

create table medical_card
(
    id bigserial not null,
    patient_id bigint,
    primary key (id),
    foreign key (patient_id) references patient_ref (id)
);

create table medical_record
(
    id bigserial not null,
    doctor varchar(255) not null,
    date date not null,
    record varchar(255),
    medical_card_id bigint not null,
    primary key (id),
    foreign key (medical_card_id) references medical_card (id)
);

create table analyzes
(
    id bigserial not null,
    date timestamp(6),
    name varchar(255),
    result varchar(255),
    medical_record_id bigint,
    primary key (id),
    foreign key (medical_record_id) references medical_record (id)
);
