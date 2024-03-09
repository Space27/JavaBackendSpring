create table if not exists link
(
    id            bigint generated always as identity,
    url           text                     not null,

    last_check_at timestamp with time zone not null default current_timestamp,

    created_at    timestamp with time zone not null default current_timestamp,

    primary key (id),
    unique (url)
);
