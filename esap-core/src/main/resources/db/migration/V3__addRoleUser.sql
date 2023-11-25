create table role_user
(
    id bigserial not null,
    role_id bigint not null,
    user_id bigint not null,
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    primary key (id)
);
INSERT INTO role_user (role_id, user_id)
SELECT role_id, doctor_id FROM role_doctor;
drop table role_doctor;
