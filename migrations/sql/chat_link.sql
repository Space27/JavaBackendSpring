create table if not exists chat_link
(
    link_id    bigint                   not null references link on delete cascade,
    chat_id    bigint                   not null references chat on delete cascade,

    created_at timestamp with time zone not null default current_timestamp,

    primary key (link_id, chat_id)
);
