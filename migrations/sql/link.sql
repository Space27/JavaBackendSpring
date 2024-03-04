create table if not exists link
(
    id          bigint generated always as identity,
    url         text                     not null,

    modified_at timestamp with time zone not null,

    created_at  timestamp with time zone not null,

    primary key (id),
    unique (url)
);
