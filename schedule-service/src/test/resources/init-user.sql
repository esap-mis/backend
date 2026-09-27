insert into doctor_ref (id, first_name, patronymic, last_name, specialization, gender, clinic_id) values
(10, 'Иван', 'Иванович', 'Иванов', 'Терапевт', 1, 10),
(11, 'Пётр', 'Петрович', 'Петров', 'Хирург', 1, 10);

insert into patient_ref (id, first_name, patronymic, last_name, birth_date, gender, address, phone_number, email, clinic_id) values
(10, 'Иван', 'Иванович', 'Иванов', '1990-05-15', 1, 'ул. Пушкина, 10', '+7(999)123-45-67', 'ivanov@mail.ru', 10),
(12, 'Сидор', 'Сидорович', 'Сидоров', '1985-03-01', 1, 'ул. Мира, 1', '+7(999)000-00-00', 'sidorov@mail.ru', 10);
