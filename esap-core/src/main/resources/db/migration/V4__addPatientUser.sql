alter table patients add constraint patients_fk foreign key (id) references users(id);