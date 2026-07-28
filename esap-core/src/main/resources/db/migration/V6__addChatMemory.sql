create table if not exists spring_ai_chat_memory (
    conversation_id varchar(255) not null,
    content varchar(10000) not null,
    type varchar(50) not null,
    timestamp timestamp default current_timestamp not null,
    primary key (conversation_id, timestamp)
);

create index if not exists idx_conversation_id on spring_ai_chat_memory(conversation_id);
create index if not exists idx_timestamp on spring_ai_chat_memory(timestamp);