create table documents
(
    id bigserial primary key,
    name varchar(255) not null,
    type varchar(255) not null,
    path varchar(255) not null,
    upload_date date not null
);