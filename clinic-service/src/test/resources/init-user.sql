insert into patient_ref (id, first_name, patronymic, last_name, birth_date, gender, address, phone_number, email, clinic_id) values
(10, 'Иван', 'Иванович', 'Иванов', '1990-05-15', 1, 'ул. Пушкина, 10', '+7(999)123-45-67', 'ivanov@mail.ru', 10);

insert into doctor_ref (id, first_name, patronymic, last_name, specialization, gender, clinic_id) values
(10, 'Иван', 'Иванович', 'Иванов', 'Терапевт', 1, 10);

insert into medical_card (id, patient_id) values
(10, 10);
