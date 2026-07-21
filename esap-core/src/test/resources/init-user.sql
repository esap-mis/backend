insert into clinics (id, address, name, phone_number) values
(10, 'Владимир, ул. Горького, д. 12', 'Поликлиника №1', '+7(999)123-45-67');

insert into users (id, login, password, clinic_id, gender) values
(10, 'admin', '$2a$10$hvXQx3dKPOqMYKGNM8XLtuMA1sMvRHBoPIBKtp6wps0d63KE7REVm', 10, 1);

insert into doctors (id, first_name, last_name, patronymic, specialization) values
(10, 'Test1', 'Иванов', 'Иванович', 'Терапевт');

insert into role (id, name) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_CHIEF_DOCTOR'),
(3, 'ROLE_DOCTOR'),
(4, 'ROLE_REGISTRANT'),
(5, 'ROLE_LABORATORY'),
(6, 'ROLE_PATIENT');

insert into role_user (role_id, user_id) values
(2, 10);

insert into patients (id, address, birth_date, email, first_name, last_name, patronymic, phone_number) values
(10, 'ул. Пушкина, д. 10, кв. 5', '1990-05-15', 'ivanov@mail.ru', 'Иван', 'Иванов', 'Иванович', '+7(999)123-45-67');

insert into medical_card (id, patient_id) values
(10, 10);