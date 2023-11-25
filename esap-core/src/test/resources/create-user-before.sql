delete from role_user;
delete from medical_card;
delete from patients;
delete from doctors;
delete from users;
delete from clinics;

alter sequence doctors_id_seq restart with 1;
alter sequence users_id_seq restart with 1;
alter sequence clinics_id_seq restart with 1;
alter sequence patients_id_seq restart with 1;
alter sequence medical_card_id_seq restart with 1;

insert into clinics (address, name, phone_number) values
('Владимир, ул. Горького, д. 12', 'Поликлиника №1', '+7(999)123-45-67');

insert into users (login, password) values
('admin', '$2a$10$hvXQx3dKPOqMYKGNM8XLtuMA1sMvRHBoPIBKtp6wps0d63KE7REVm');

insert into doctors (id, first_name, last_name, patronymic, specialization, clinic_id, gender) values
(1, 'Test1', 'Иванов', 'Иванович', 'Терапевт', 1, 1);

insert into role_user (role_id, user_id) values (2, 1);

insert into patients (address, birth_date, email, first_name, gender, last_name, patronymic, phone_number, clinic_id) values
('ул. Пушкина, д. 10, кв. 5', '1990-05-15', 'ivanov@mail.ru', 'Иван', 1, 'Иванов', 'Иванович', '+7(999)123-45-67', 1);

insert into medical_card (patient_id) values (1);