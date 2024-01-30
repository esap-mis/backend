create table documents
(
    id bigserial primary key,
    file_name varchar(255) not null,
    file_type varchar(255) not null,
    key varchar(255) not null,
    size bigserial not null,
    upload_date date not null,
    medical_record_id bigint,
    FOREIGN KEY (medical_record_id) REFERENCES medical_record (id)
);