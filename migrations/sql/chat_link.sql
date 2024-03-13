create table if not exists chat_link
(
    id         bigint generated always as identity,

    link_id    bigint
        constraint link_ref not null references link,
    chat_id    bigint
        constraint chat_ref not null references chat,

    created_at timestamp with time zone not null default current_timestamp,

    primary key (id),
    unique (link_id, chat_id)
);
