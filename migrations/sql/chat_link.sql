create table if not exists chat_link
(
    link_id    bigint                   not null references link,
    chat_id    bigint                   not null references chat,

    created_at timestamp with time zone not null,

    primary key (link_id, chat_id)
);
