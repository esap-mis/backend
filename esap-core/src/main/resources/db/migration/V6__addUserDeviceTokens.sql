create table user_device_tokens
(
    id bigserial primary key,
    token varchar(255),
    user_id bigint not null,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
