-- Создаёт отдельную базу на каждый сервис: данные перестали быть общей таблицей,
-- а сервисы больше не могут случайно достать чужую сущность.
-- Скрипт выполняется только при первом старте на пустом томе.
CREATE DATABASE esap_auth;
CREATE DATABASE esap_clinic;
CREATE DATABASE esap_schedule;
CREATE DATABASE esap_core;
