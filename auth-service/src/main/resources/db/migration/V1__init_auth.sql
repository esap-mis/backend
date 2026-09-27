create table clinics
(
    id bigserial not null,
    address varchar(255) not null,
    name varchar(255) not null,
    phone_number varchar(255) not null,
    primary key (id)
);

create table role
(
    id bigserial not null,
    name varchar(255) not null,
    primary key (id)
);

insert into role (name)
values ('ROLE_ADMIN'),
       ('ROLE_CHIEF_DOCTOR'),
       ('ROLE_DOCTOR'),
       ('ROLE_REGISTRANT'),
       ('ROLE_LABORATORY'),
       ('ROLE_PATIENT');

create table users
(
    id bigserial not null,
    login varchar(255),
    password varchar(255),
    clinic_id bigint not null,
    gender integer not null check (gender <= 2 and gender >= 1),
    foreign key (clinic_id) references clinics (id),
    primary key (id)
);

create table role_user
(
    id bigserial not null,
    role_id bigint not null,
    user_id bigint not null,
    foreign key (role_id) references role (id),
    foreign key (user_id) references users (id),
    primary key (id)
);

create table doctors
(
    id bigint not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    patronymic varchar(100) not null,
    specialization varchar(255) not null,
    foreign key (id) references users (id),
    primary key (id)
);

create table patients
(
    id bigint not null,
    address varchar(200),
    birth_date date not null,
    email varchar(100),
    first_name varchar(100),
    last_name varchar(100),
    patronymic varchar(100),
    phone_number varchar(20),
    foreign key (id) references users (id),
    primary key (id)
);
