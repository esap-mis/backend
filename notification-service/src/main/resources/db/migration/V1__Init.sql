create table user_device_tokens
(
    id bigserial primary key,
    fcm_token varchar(255) not null,
    user_id bigint not null,
    status varchar(255) not null
);
