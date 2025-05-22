create table roles
(
    id   BIGSERIAL PRIMARY KEY,
    role varchar(256) not null
);

create table users
(
    id       BIGSERIAL PRIMARY KEY,
    email    varchar(256) unique not null,
    password varchar(256)        not null
);

CREATE TABLE users_roles
(
    user_id INT REFERENCES users (id),
    role_id INT REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);
insert into roles (id, role)
values (1, 'USER'),
       (2, 'ADMIN');



insert into users (id, email, password)
values (1, 'user@user.user', '$2a$12$PrIEGbui94TrFaqbGVWSieiIvnhjna2e4Rvz3wn4kWIlKB.IUs3Mq'),
       (2, 'admin@admin.admin', '$2a$12$MKD5hIXD9nO8HZsGGNzRQ..s1DmZKOqiwJuglb1BnVWbquYI1fTM2');

insert into users_roles (user_id, role_id)
values (1, 1),
       (2, 2);
