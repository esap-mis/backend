create table users
(
    id bigserial not null,
    login varchar(255),
    password varchar(255),
    clinic_id bigint not null,
    gender integer not null check (gender<=2 AND gender>=1),
    FOREIGN KEY (clinic_id) REFERENCES clinics (id),
    primary key (id)
);
INSERT INTO users (id, login, password)
SELECT id, login, password FROM doctors;
alter table doctors drop password;
alter table doctors drop login;
alter table doctors add constraint doctors_fk foreign key (id) references users(id);
